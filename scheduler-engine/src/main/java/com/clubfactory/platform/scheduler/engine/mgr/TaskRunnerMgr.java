package com.clubfactory.platform.scheduler.engine.mgr;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.common.utils.DateUtils;
import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import com.clubfactory.platform.scheduler.core.dto.TaskDto;
import com.clubfactory.platform.scheduler.core.enums.ZKNodeType;
import com.clubfactory.platform.scheduler.core.service.ICommonService;
import com.clubfactory.platform.scheduler.core.service.impl.TaskMonitorService;
import com.clubfactory.platform.scheduler.core.thread.Stopper;
import com.clubfactory.platform.scheduler.core.utils.*;
import com.clubfactory.platform.scheduler.core.vo.MacroVarVO;
import com.clubfactory.platform.scheduler.core.vo.ScriptVO;
import com.clubfactory.platform.scheduler.core.vo.SlotMap;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.enums.JobCycleTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.*;
import com.clubfactory.platform.scheduler.engine.config.ExecutorConfig;
import com.clubfactory.platform.scheduler.engine.dao.DataAccessFactory;
import com.clubfactory.platform.scheduler.engine.runner.TaskRunner;
import com.clubfactory.platform.scheduler.engine.utils.AbstractZKClient;
import com.clubfactory.platform.scheduler.engine.utils.TaskLock;
import com.clubfactory.platform.scheduler.spi.utils.ProcessUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.shaded.com.google.common.collect.Queues;
import org.apache.curator.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum.*;
import static org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent.Type.*;


/**
 * Task Instance Runner Manager
 * single instance
 *
 * @author xiejiajun
 */
public class TaskRunnerMgr extends AbstractZKClient {

    private static final Logger logger = LoggerFactory.getLogger(TaskRunnerMgr.class);


    private static final ThreadFactory DEFAULT_THREAD_FACTORY = ThreadUtils.newGenericThreadFactory("Worker-Main-Thread");


    /**
     * 用于存放正在运行的Task实例
     */
    private final Map<Long, TaskVO> runningTasks = Maps.newConcurrentMap();


    /**
     * 用于存放定期从ZK上拉取的需要kill的任务
     */
    private final BlockingQueue<Long> killedTaskQueue = Queues.newLinkedBlockingQueue();


    /**
     * worker znode
     */
    private String workerZNode = null;


    /**
     * TaskRunner
     */
    private static TaskRunnerMgr taskRunnerMgr = null;


    /**
     * fetch task executor service
     */
    private ExecutorService fetchTaskExecutorService;

    /**
     * exec task executor service
     */
    private ExecutorService workerExecService;


    /**
     * kill executor service
     */
    private ExecutorService killExecutorService;

    /**
     * task 实例监听器
     */
    private PathChildrenCache waitingTaskWatcher;

    /**
     * worker节点监听器
     */
    private PathChildrenCache workerWatcher;

    /**
     * worker相关配置
     */
    @Setter
    private ExecutorConfig executorConfig;

    private ICommonService dataAccessProxy;

    private final Map<String, Integer> typeSlotMap = Maps.newConcurrentMap();

    private final Map<String, Integer> currentUsedSlotMap = Maps.newConcurrentMap();

    /**
     * 上一次检测slot数量的时间
     */
    private volatile long lastSlotsCheckTime;


    /**
     * 由于可用线程不足导致任务被拒绝的日志打印控制器
     */
    private AtomicInteger threadLimitRefuseCounter;

    /**
     * 由于系统资源（CPU/MEM）不足导致任务被拒绝的日志打印控制器
     */
    private AtomicInteger sysResourceLimitRefuseCounter;

    /**
     * 由于slots不足导致任务被拒绝的日志打印控制器
     */
    private AtomicInteger slotLimitRefuseCounter;

    private AtomicInteger logicMemoryLimitRefuseCounter;

    private final int logPrintThreshold = 5;

    /**
     * 资源获取时加锁，降低高并发时获取到的系统内存不准确发生概览
     */
    private final Object resourceLock = new Object();

    private LogicMemoryMgr logicMemoryMgr;

    private double maxAvgCpuLoad;
    private double minReservedMemory;
    private final Map<PathChildrenCacheEvent.Type, WorkerEventHandler> workerEventHandlerMap = Maps.newHashMap();
    private final Map<PathChildrenCacheEvent.Type, TaskEventHandler> taskEventHandlerMap = Maps.newHashMap();
    private final Map<TaskStatusEnum, TaskReqHandler> taskReqHandlerMap = Maps.newHashMap();


    /**
     * init
     */
    @Override
    public void init() {
        super.init();
        this.dataAccessProxy = DataAccessFactory.getDataAccessProxy();
        this.logicMemoryMgr = LogicMemoryMgr.getInstance();
        this.threadLimitRefuseCounter = new AtomicInteger(0);
        this.sysResourceLimitRefuseCounter = new AtomicInteger(0);
        this.slotLimitRefuseCounter = new AtomicInteger(0);
        this.logicMemoryLimitRefuseCounter = new AtomicInteger(0);
        this.maxAvgCpuLoad = PropertyUtils.getDouble(Constants.WORKER_MAX_CPULOAD_AVG,
                Constants.DEFAULT_WORKER_CPU_LOAD);
        this.minReservedMemory = PropertyUtils.getDouble(Constants.WORKER_RESERVED_MEMORY,
                Constants.DEFAULT_WORKER_RESERVED_MEMORY);
        this.refreshSlotMap();
        this.lastSlotsCheckTime = System.currentTimeMillis();
        // 初始化slots
        Integer currWorkerSlots = dataAccessProxy.getWorkerSlots(OSUtils.getHost());
        currWorkerSlots = currWorkerSlots != null && currWorkerSlots > 0 ? currWorkerSlots : 0;
        // fetch task executor service init
        fetchTaskExecutorService = com.clubfactory.platform.scheduler.core.thread.ThreadUtils
                .newDaemonSingleThreadExecutor("Worker-Fetch-Thread-Executor");
        // exec task executor service init
        int workerExecNums = executorConfig.getExecThreadNum();
        workerExecNums = workerExecNums > currWorkerSlots ? workerExecNums : currWorkerSlots;
        int maximumPoolSize = workerExecNums * 2 + 1;
        logger.info("任务执行线程池 核心线程数 {} 最大线程数 {} ", workerExecNums, maximumPoolSize);
        workerExecService = com.clubfactory.platform.scheduler.core.thread.ThreadUtils
                .newDaemonFixedThreadExecutor("Worker-Exec-Thread-Executor", workerExecNums, maximumPoolSize);
        // kill task executor service init
        int killThreadNums = executorConfig.getKillThreadNum();
        killExecutorService = com.clubfactory.platform.scheduler.core.thread.ThreadUtils
                .newDaemonFixedThreadExecutor("Worker-Kill-Thread-Executor", killThreadNums);
        this.initEventHandler();
        // init system znode
        this.initSystemZNode();
        // monitor worker
        this.listenerWorker();
        // register worker
        this.registerWorker();
        this.clearFakeRunningTask();
        // monitor taskInstance (执行 & Kill)
        this.listenTaskInstance();
        Runnable killTaskThread = createKillTaskThread();
        killExecutorService.execute(killTaskThread);
        if (PropertyUtils.getBoolean(Constants.SYSTEM_MONITOR_ENABLE, true)) {
            Thread systemMonitor = new Thread(createSystemMonitorThread());
            systemMonitor.setName("system-resource-monitor-thread");
            systemMonitor.start();
        }
        Thread dbConnTestThread = new Thread(createDBConnTestThread());
        dbConnTestThread.setName("db-conn-test-thread");
        dbConnTestThread.start();
        Thread slotRefreshThread = new Thread(createSlotRefreshThread());
        slotRefreshThread.setName("slot-refresh-thread");
        slotRefreshThread.start();
    }

    private void initEventHandler() {
        this.workerEventHandlerMap.put(CHILD_ADDED, event -> logger.info("node added : {}", event.getData().getPath()));
        this.workerEventHandlerMap.put(CHILD_REMOVED, event -> {
            String path = event.getData().getPath();
            if (workerZNode.equals(path)) {
                String serverHost = getHostByEventDataPath(path);
                // 若是当前worker挂掉，进行Stopper回调
                checkServerSelfDead(serverHost, ZKNodeType.WORKER);
            }
            logger.info("worker node: {} had removed",path);
        });

        this.taskEventHandlerMap.put(CHILD_ADDED, this::pollAndProcessTask);
        this.taskEventHandlerMap.put(CHILD_UPDATED, this::pollAndProcessTask);

        this.taskReqHandlerMap.put(SCHEDULED, this::submitTask);
        this.taskReqHandlerMap.put(KILLING, (taskDto, path, taskId) -> this.killTask(taskId, path));
        this.taskReqHandlerMap.put(MANUAL_SUCCESS, (taskDto, path, taskId) -> this.killTask(taskId, path));
    }

    /**
     * clear fake running task
     */
    private void clearFakeRunningTask() {
        List<TaskVO> fakeRunningTaskList = dataAccessProxy.listRunningTasksByHost(OSUtils.getHost())
                .stream()
                .filter(task -> task.getCycleType() != JobCycleTypeEnum.REAL_TIME)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fakeRunningTaskList)) {
            return;
        }
        for (TaskVO taskVO : fakeRunningTaskList) {
            try {
                ProcessUtils.kill(taskVO);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * get taskRunnerMgr
     *
     * @return
     */
    public static synchronized TaskRunnerMgr getTaskRunnerMgr() {
        if (taskRunnerMgr == null) {
            taskRunnerMgr = new TaskRunnerMgr();
        }
        return taskRunnerMgr;
    }


    /**
     * register worker
     */
    private void registerWorker() {
        try {
            String serverPath = registerServer(ZKNodeType.WORKER);
            if (StringUtils.isEmpty(serverPath)) {
                System.exit(-1);
            }
            workerZNode = serverPath;
        } catch (Exception e) {
            logger.error("register worker failure : " + e.getMessage(), e);
            System.exit(-1);
        }
    }


    /**
     * 监听任务实例节点，有任务更新时，拉取任务执行（一次拉一个）
     * monitor task instance
     */
    private void listenTaskInstance() {

        waitingTaskWatcher = new PathChildrenCache(zkClient, getZNodeParentPath(ZKNodeType.TASK_INSTANCE), true);
        try {
            waitingTaskWatcher.start();
            waitingTaskWatcher.getListenable().addListener(
                    (CuratorFramework client, PathChildrenCacheEvent event) -> {
                        TaskEventHandler eventHandler = this.taskEventHandlerMap.get(event.getType());
                        if (eventHandler != null) {
                            eventHandler.handle(event);
                        }
                    },
                    fetchTaskExecutorService);
        } catch (Exception e) {
            logger.error("monitor waiting task failed : " + e.getMessage(), e);
        }
    }

    /**
     * monitor worker
     */
    private void listenerWorker() {
        workerWatcher = new PathChildrenCache(zkClient, getZNodeParentPath(ZKNodeType.WORKER), true, DEFAULT_THREAD_FACTORY);
        try {
            workerWatcher.start();
            workerWatcher.getListenable().addListener((client, event) -> {
                WorkerEventHandler eventHandler = this.workerEventHandlerMap.get(event.getType());
                if (eventHandler != null) {
                    eventHandler.handle(event);
                }
            });
        } catch (Exception e) {
            logger.error("monitor worker failed : " + e.getMessage(), e);
        }

    }

    /**
     * get worker znode
     *
     * @return
     */
    public String getWorkerZNode() {
        return workerZNode;
    }


    /**
     * 将作业标记为Running
     *
     * @param taskVO
     */
    private synchronized void markRunning(TaskVO taskVO) {
        this.runningTasks.put(taskVO.getId(), taskVO);
        String feature = taskVO.taskCategory();
        Integer currentUsedSlot = this.currentUsedSlotMap.get(feature);
        if (currentUsedSlot == null) {
            currentUsedSlot = 0;
        }
        currentUsedSlot += 1;
        this.currentUsedSlotMap.put(feature, currentUsedSlot);
    }

    /**
     * 移除运行状态
     *
     * @param taskVO
     */
    public synchronized void unsetRunning(TaskVO taskVO) {
        try {
            runningTasks.remove(taskVO.getId());
        } catch (Exception e) {
            logger.error("从 running tasks 队列移除任务失败");
        }
        String feature = taskVO.taskCategory();
        Integer currentUsedSlot = this.currentUsedSlotMap.get(feature);
        if (currentUsedSlot == null || currentUsedSlot <= 0) {
            return;
        }
        currentUsedSlot -= 1;
        this.currentUsedSlotMap.put(feature, currentUsedSlot);
    }

    public int getRunningSize() {
        return runningTasks.size();
    }

    /**
     * 拉取taskInstance执行
     */
    public void pollAndProcessTask(PathChildrenCacheEvent event) {
        if (Stopper.isStoped()) {
            logger.info("worker node {} is already stop, refuse the task", OSUtils.getHost());
            return;
        }
        try {
            // task/worker_ip/${taskId}
            String path = event.getData().getPath();
            if (StringUtils.isBlank(path) || !checkZNodeExists(path)) {
                logger.info("task 路径为空");
                return;
            }
            Long taskId = NumberUtils.stringToLong(StringUtils.substringAfterLast(path, Constants.SINGLE_SLASH));
            TaskDto taskDto = getZNodeData(path, TaskDto.class);
            if (taskDto == null || taskId == null || taskDto.getStatus() == null) {
                logger.info("task 数据或者状态为空");
                return;
            }
            TaskReqHandler reqHandler = this.taskReqHandlerMap.get(taskDto.getStatus());
            if (reqHandler != null) {
                reqHandler.handle(taskDto, path, taskId);
            }
        } catch (Exception e) {
            logger.error("runtime exception", e);
        }

    }

    /**
     * 提交任务
     * @param taskDto
     * @param path
     * @param taskId
     */
    private void submitTask(TaskDto taskDto, String path, Long taskId) {
        boolean passThreadCheck = checkThreadCount((ThreadPoolExecutor) workerExecService);
        if (isRunning(taskId) || !passThreadCheck) {
            if (!passThreadCheck) {
                this.deleteZNode(path);
            }
            return;
        }
        TaskLock taskLock = new TaskLock(this.getZkClient(), taskId);
        if (!taskLock.tryLock(5)) {
            logger.info("获取task {}锁失败，退出执行", taskId);
            return;
        }
        TaskVO task = null;
        int taskMbMemPeak = 0;
        boolean hadAcquireMemory = false;
        try {
            // 从DB获取对应的task实例
            task = dataAccessProxy.getTaskInstance(taskId);
            if (task == null) {
                logger.info("DB中不存在id为{}的task实例", taskId);
                this.deleteZNode(path);
                return;
            }
            if (task.getStatus() == TaskStatusEnum.RUNNING) {
                logger.info("DB中id为{}的task实例已经是RUNNING状态", taskId);
                this.deleteZNode(path);
                return;
            }
            // ip设成本地
            task.setIp(OSUtils.getHost());
            this.initJobInfo(task);
            taskMbMemPeak = logicMemoryMgr.getTaskMemoryPeak(task);
            logger.info("taskId: {}, taskMbMemPeak: {}, jobType: {}, JobCategoryEnum: {}, jobConfig: {}", task.getId(), taskMbMemPeak, task.getType(), task.getCategory(), task.getJobConf());
            if (!checkSlotResource(task) || !checkSystemLoad() || !checkLogicMemory(task,taskMbMemPeak)) {
                this.deleteZNode(path);
                return;
            }
            hadAcquireMemory = true;

            task.setCommandType(taskDto.getCommandType());
            task.setVersion(taskDto.getVersion());
            task.setFileName(taskDto.getFileName());
            task.setHasBlockDqcChildren(taskDto.getIsBlock());
            task.setZNodePath(path);

            ScriptVO scriptVO = dataAccessProxy.getScriptInfoById(task.getScriptId());
            String scriptBasePath = null;
            if (scriptVO != null && StringUtils.isNotBlank(scriptVO.getScriptBasePath())){
                scriptBasePath = scriptVO.getScriptBasePath();
            }

            task.setScriptBasePath(scriptBasePath);
            // 更新taskInstance状态：RUNNING
            task.setStatus(TaskStatusEnum.RUNNING);
            this.setTaskStatus(task);
            logger.info("Task{} 状态置为RUNNING", taskId);
            // 置为Running
            this.markRunning(task);
            logger.info("开始提交任务：{},zNode :{}", task.getId(), path);
            workerExecService.submit(new TaskRunner(task, this, taskMbMemPeak));

            logger.info("任务【{}】提交成功", task.getId());
        } catch (Exception e) {
            logger.error("runtime exception", e);
            runningTasks.remove(taskId);
            this.deleteZNode(path);
            if (hadAcquireMemory && !task.isRunOnTmpEmr()) {
                logicMemoryMgr.releaseMemory(taskMbMemPeak);
            }
        } finally {
            taskLock.unlock(false);
        }
    }

    /**
     * Kill任务
     * @param taskId
     * @param path
     */
    private void killTask(Long taskId, String path) {
        logger.info("收到任务 {} 的 kill 请求", taskId);
        if (isRunning(taskId)) {
            if (killedTaskQueue.contains(taskId)) {
                logger.info("任务 {} 已在 kill 队列中", taskId);
                return;
            }
            if (!killedTaskQueue.offer(taskId)) {
                logger.error("killing queue is outIndex ");
            }
        } else {
            // 删除task对应的zNode
            this.deleteZNode(path);
            logger.info("{} deleted task {} from zk success", OSUtils.getHost(), path);
            TaskVO taskVO = dataAccessProxy.getTaskInstance(taskId);
            if (taskVO == null) {
                return;
            }
            if (taskVO.getStatus() != TaskStatusEnum.RUNNING
                    && taskVO.getStatus() != KILLING
                    && taskVO.getStatus() != MANUAL_SUCCESS) {
                logger.info("任务{}状态为{},不支持KILL", taskId, taskVO.getStatus().name());
                return;
            }
            TaskStatusEnum status = TaskStatusEnum.KILLED;
            if (taskVO.getStatus() == MANUAL_SUCCESS) {
                status = TaskStatusEnum.SUCCESS;
            }

            TaskMonitor taskMonitor = SpringBean.getBean(TaskMonitorService.class).getByTaskId(taskId);
            if (taskMonitor != null) {
                logger.info("当前任务为流任务,kill Yarn Application :{} ", taskMonitor.getYarnAppId());
                ProcessUtils.killYarnJobByAppId(taskVO, taskMonitor.getYarnAppId());
            }
            logger.info("当前worker节点找不到ID为{}的Task，无需Kill，直接将DB中RUNNING/KILLING状态改为{}",
                    taskId, status);
            this.setTaskEndStatus(taskVO, status);
        }
    }

    /**
     * 格式化JobConf信息
     * @param task
     */
    private void initJobInfo(TaskVO task) {
        BaseJob jobInfo = null;
        Boolean runOnTempEmr = null;
        Map<String,String> jobConf = null;
        try {
            if (!task.getIsTemp()) {
                jobInfo = dataAccessProxy.getParamsByJobId(task.getJobId());
            }else {
                jobInfo = dataAccessProxy.getParamsByIdFromJob(task.getJobId());
            }
            task.setBaseJob(jobInfo);
            jobConf = JSONUtils.toMap(jobInfo.getJobConf());
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        if (jobConf == null){
            jobConf = Maps.newHashMap();
        }
        if (jobInfo != null){
            runOnTempEmr = jobInfo.getRunOnTmpEmr();
        }

        List<MacroVarVO> globalParams = dataAccessProxy.listGlobalMacroVars();
        if (CollectionUtils.isEmpty(globalParams)) {
            globalParams = Lists.newArrayList();
        }
        task.setGlobalParams(globalParams);

//        JobType jobType = dataAccessProxy.getJobType(jobInfo.getJobTypeId());
        JobType jobType = dataAccessProxy.getJobTypeByName(task.getType(), task.getCategory());
        task.setJobTypeInfo(jobType);

        if (jobType != null && jobType.getIsClusterJob() && task.getClusterId() != null) {
            Cluster cluster = dataAccessProxy.getClusterInfoById(task.getClusterId());
            task.setCluster(cluster);
        }

        runOnTempEmr = Optional.ofNullable(runOnTempEmr).orElse(false);
        task.setRunOnTmpEmr(runOnTempEmr);
        task.setJobConf(jobConf);
    }


    /**
     * 判断是否在Running队列中
     *
     * @param taskId
     * @return
     */
    private boolean isRunning(Long taskId) {
        boolean isRunning = runningTasks.get(taskId) != null;
        if (isRunning) {
            logger.info("ID为{}的Task正在运行", taskId);
        }
        return isRunning;
    }


    /**
     * 检查对应类型的slot数量是否充足
     *
     * @param taskVO
     * @return
     */
    private synchronized boolean checkSlotNum(TaskVO taskVO) {
        String feature = taskVO.taskCategory();
        Integer currentUsedSlot = this.currentUsedSlotMap.get(feature);
        if (currentUsedSlot == null) {
            currentUsedSlot = 0;
        }
        Integer totalSlot = this.typeSlotMap.get(feature);
        if (totalSlot == null) {
            return false;
        }
        return totalSlot > currentUsedSlot;
    }

    /**
     * 检查CPU、Mem以及可用slot是否充足
     *
     * @return
     */
    private boolean checkSlotResource(TaskVO taskVO) {
        if (!checkSlotNum(taskVO)) {
            // 任务运行数量超过slots数时，刷新一次worker的slots,然后再比较一次
            refreshSlots();
            if (!checkSlotNum(taskVO)) {
                int refuseCount = slotLimitRefuseCounter.getAndAdd(1);
                // 大于10次打印日志并将计数器清零
                if (refuseCount >= logPrintThreshold) {
                    String feature = taskVO.taskCategory();
                    logger.warn("正在运行的任务类型对应的任务数{}超过对应类型最大slot数{}，暂停接收任务,等待下次触发",
                            currentUsedSlotMap.get(feature), typeSlotMap.get(feature));
                    slotLimitRefuseCounter.set(0);
                }
                return false;
            }
        }
        slotLimitRefuseCounter.set(0);
        return true;
    }


    /**
     * 检查系统资源（CPU/MEM) 使用百分比
     *
     * @return
     */
    private boolean checkSystemLoad() {
        double loadAverage;
        double availablePhysicalMemorySize;
        synchronized (resourceLock) {
            loadAverage = OSUtils.loadAverage();
            availablePhysicalMemorySize = OSUtils.availablePhysicalMemorySize();
        }
        if (loadAverage >= this.maxAvgCpuLoad || availablePhysicalMemorySize <= this.minReservedMemory) {
            int refuseCount = sysResourceLimitRefuseCounter.addAndGet(1);
            // 大于10次打印日志并将计数器清零
            if (refuseCount >= logPrintThreshold) {
                logger.warn("CPU负载过高或者系统可用内存(G)过低, 当前系统可用内存(G)为:{}, CPU平均负载为:{}",
                        availablePhysicalMemorySize, loadAverage);
                sysResourceLimitRefuseCounter.set(0);
            }
            return false;
        }
        sysResourceLimitRefuseCounter.set(0);
        return true;
    }


    /**
     * 检查系统内存池资源是否充足
     *
     * @param task
     * @param mbMemSize
     * @return
     */
    private boolean checkLogicMemory(TaskVO task,int mbMemSize) {
        if (task.isRunOnTmpEmr()){
            return true;
        }
        if (!logicMemoryMgr.checkAndAcquireMemoryGB(mbMemSize)) {
            int refuseCount = logicMemoryLimitRefuseCounter.addAndGet(1);
            if (refuseCount >= this.logPrintThreshold) {
                logicMemoryLimitRefuseCounter.set(0);
                logger.info("内存申请失败，当前系统已分配内存过多，可用内存不足,系统可用内存:{}GB，已分配内存:{}GB",
                        logicMemoryMgr.availableMemoryGB(),
                        logicMemoryMgr.currentAssignedMemoryGB());
            }
            return false;
        }
        logicMemoryLimitRefuseCounter.set(0);
        return true;
    }

    /**
     * 检查execPool中可用线程
     *
     * @param poolExecutor
     * @return
     */
    private boolean checkThreadCount(ThreadPoolExecutor poolExecutor) {
        int activeCount = poolExecutor.getActiveCount();
        if (activeCount >= poolExecutor.getCorePoolSize()) {
            int refuseCount = threadLimitRefuseCounter.addAndGet(1);
            // 大于10次打印日志并将计数器清零
            if (refuseCount >= logPrintThreshold) {
                logger.warn("任务执行线程池当前可用线程不足 , 活动线程数 : {} , Worker线程总数 : {}", activeCount, poolExecutor.getCorePoolSize());
                threadLimitRefuseCounter.set(0);
            }
            return false;
        }
        threadLimitRefuseCounter.set(0);
        return true;
    }

    /**
     * 从DB拉取当前Worker的最新slots配置
     */
    private synchronized void refreshSlots() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSlotsCheckTime >= Constants.SLOTS_CHECK_INTERVAL) {
            this.refreshSlotMap();
            this.lastSlotsCheckTime = currentTime;
        }
    }

    /**
     * 刷新slot
     */
    private synchronized void refreshSlotMap() {
        List<SlotMap> slotMaps = dataAccessProxy.getWorkerSlotMap(OSUtils.getHost());
        if (CollectionUtils.isEmpty(slotMaps)) {
            logger.warn("slot map is empty");
            return;
        }
        for (SlotMap slotMap : slotMaps) {
            if (slotMap == null || slotMap.getFeature() == null) {
                continue;
            }
            if (slotMap.getSlot() == null) {
                slotMap.setSlot(0);
            }
            this.typeSlotMap.put(slotMap.getFeature(), slotMap.getSlot());
        }
        logger.info("refresh slot map success");
    }

    /**
     * kill正在运行的Job
     */
    private void killRunningTasks() {
        for (TaskVO taskVO : runningTasks.values()) {
            if (taskVO.getPid() != null) {
                ProcessUtils.kill(taskVO);
            }
        }
        runningTasks.clear();
    }


    /**
     * kill process thread implement
     *
     * @return
     */
    private Runnable createKillTaskThread() {
        return () -> {
            while (Stopper.isRunning()) {
                try {
                    Thread.sleep(Constants.SLEEP_TIME_MILLIS);
                } catch (InterruptedException e) {
                    logger.error("interrupted exception", e);
                }
                Long taskId;
                while ((taskId = killedTaskQueue.poll()) != null) {
                    logger.info("开始Kill任务 {} ", taskId);
                    TaskVO taskInstance = runningTasks.get(taskId);
                    if (taskInstance != null) {
                        try {
                            boolean killedSuccess = ProcessUtils.kill(taskInstance);
                            // 将杀掉的任务状态置为KILLED
                            if (killedSuccess) {
                                taskInstance.setStatus(TaskStatusEnum.KILLED);
                            }
                            if (taskInstance.isTaskExit()){
                                this.setTaskEndStatus(taskInstance,TaskStatusEnum.KILLED);
                            }
                        } catch (Exception e) {
                            logger.error("runtime exception", e);
                        }
                    } else {
                        logger.info("未找到正在运行的 id 为 {} 的任务", taskId);
                    }
                }
            }
        };
    }

    private Runnable createSystemMonitorThread() {
        return () -> {
            while (Stopper.isRunning()) {
                try {
                    Thread.sleep(Constants.SYSTEM_MONITOR_INTERVAL);
                } catch (InterruptedException e) {
                    logger.error("interrupted exception", e);
                }
                try {
                    double currentLoadAverage;
                    double currentAvailablePhysicalMemorySize;
                    synchronized (resourceLock) {
                        currentLoadAverage = OSUtils.loadAverage();
                        currentAvailablePhysicalMemorySize = OSUtils.availablePhysicalMemorySize();
                    }

                    logger.info("当前CPU平均负载为：{},可用内存为: {}G", currentLoadAverage,
                            currentAvailablePhysicalMemorySize);
                } catch (Exception e) {
                    logger.error(e.getMessage(),e);
                }
            }
        };
    }


    /**
     * ETL调度机数据源连接测试线程
     *
     * @return
     */
    private Runnable createDBConnTestThread() {
        return () -> {
            while (Stopper.isRunning()) {
                try {
                    dataAccessProxy.refreshDBConnState(OSUtils.getHost());
                } catch (Exception e) {
                    logger.error(String.format("刷新连接状态失败:%s",e.getMessage()),e);
                }
                try {
                    Thread.sleep(Constants.DB_CONN_MONITOR_INTERVAL);
                } catch (InterruptedException e) {
                    logger.error("interrupted exception", e);
                }
            }
        };
    }


    private Runnable createSlotRefreshThread() {
        return () -> {
            while (Stopper.isRunning()) {
                try {
                    Thread.sleep(Constants.WORKER_SLOT_REFRESH_INTERVAL);
                } catch (InterruptedException e) {
                    logger.error("interrupted exception", e);
                }
                try {
                    this.refreshSlotMap();
                    this.lastSlotsCheckTime = System.currentTimeMillis();
                } catch (Exception e) {
                    logger.error(String.format("刷新slot信息失败:%s",e.getMessage()),e);
                }
            }
        };
    }

    @Override
    public void release() {

        // kill 正在运行的taskInstance
        this.killRunningTasks();

        if (!fetchTaskExecutorService.isShutdown() && !fetchTaskExecutorService.isTerminated()) {
            fetchTaskExecutorService.shutdownNow();
        }
        if (!workerExecService.isTerminated() && !workerExecService.isShutdown()) {
            workerExecService.shutdownNow();
        }
        if (!killExecutorService.isTerminated() && !killExecutorService.isShutdown()) {
            killExecutorService.shutdownNow();
        }

        try {
            if (waitingTaskWatcher != null) {
                waitingTaskWatcher.close();
            }
            if (workerWatcher != null) {
                workerWatcher.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * 判断当前机器是否已经失联
     *
     * @param zNode node path
     * @throws Exception
     */
    @Override
    protected boolean checkIsDeadServer(String zNode) throws Exception {
        return !this.checkZNodeExists(zNode);
    }

    /**
     * 更新task最终状态
     */
    private void setTaskEndStatus(TaskVO taskVO, TaskStatusEnum status) {
        Task task = new Task();
        task.setId(taskVO.getId());
        Date endTime = new Date();
        if (DateUtils.after(taskVO.getEndTime(),taskVO.getExecTime())){
            endTime = taskVO.getEndTime();
        }
        task.setEndTime(endTime);
        task.setStatus(status);
        dataAccessProxy.updateTaskInfo(task);
    }

    /**
     * 置为RUNNING状态
     */
    private void setTaskStatus(TaskVO taskInstance) {
        dataAccessProxy.taskStateRunning(taskInstance.getId());
    }

    interface WorkerEventHandler {
        /**
         * 处理Worker事件
         * @param event
         */
        void handle(PathChildrenCacheEvent event);
    }

    interface TaskEventHandler {
        /**
         * 处理Task事件
         * @param event
         */
        void handle(PathChildrenCacheEvent event);
    }

    interface TaskReqHandler {
        /**
         * 处理Task请求
         * @param taskDto
         * @param path
         * @param taskId
         */
        void handle(TaskDto taskDto, String path, Long taskId);
    }

}

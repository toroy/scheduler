package com.clubfactory.platform.scheduler.engine.utils;


import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.IStoppable;
import com.clubfactory.platform.scheduler.core.enums.ZKNodeType;
import com.clubfactory.platform.scheduler.core.thread.Stopper;
import com.clubfactory.platform.scheduler.common.utils.DateUtils;
import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import com.clubfactory.platform.scheduler.core.utils.ResInfo;
import com.clubfactory.platform.scheduler.engine.config.ZookeeperConfig;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.clubfactory.platform.scheduler.common.Constants.*;


/**
 * abstract zookeeper client
 */
public abstract class AbstractZKClient {

	private static final Logger logger = LoggerFactory.getLogger(AbstractZKClient.class);

	/**
	 *  load configuration file
	 */
	@Setter
	private ZookeeperConfig zookeeperProperties;
	
	protected  CuratorFramework zkClient = null;

	protected volatile boolean isInit = false;

	/**
	 * server stop or not
	 */
	protected IStoppable stoppable = null;

	protected void init(){
		// retry strategy
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(
				zookeeperProperties.getRetryInterval(),
				zookeeperProperties.getRetryMaxTimes());

		try{
			// crate zookeeper client
			zkClient = CuratorFrameworkFactory.builder()
					.connectString(getZookeeperQuorum())
					.retryPolicy(retryPolicy)
					.sessionTimeoutMs(1000 * zookeeperProperties.getSessionTimeout())
					.connectionTimeoutMs(1000 * zookeeperProperties.getConnectionTimeout())
					.build();

			zkClient.start();
			initStateLister();

		}catch(Exception e){
			logger.error("create zookeeper connect failed : " + e.getMessage(),e);
			System.exit(-1);
		}
	}
	
	/**
	 *
	 *  register status monitoring events for zookeeper clients
	 */
	public void initStateLister(){
		if(zkClient == null) {
			return;
		}
		// add ConnectionStateListener monitoring zookeeper  connection state
		ConnectionStateListener csLister = new ConnectionStateListener() {
			
			@Override
			public void stateChanged(CuratorFramework client, ConnectionState newState) {
				logger.info("state changed , current state : " + newState.name());
				/**
				 * probably session expired
				 */
				if (newState == ConnectionState.SUSPENDED){
					logger.error("zookeeper 连接已被挂起 ");
				}
				if(newState == ConnectionState.LOST){
					// if lost , then exit
					logger.info("current zookeepr connection state : connection lost");
					stoppable.stop("current zookeepr connection state : connection lost");
				}
			}
		};
		
		zkClient.getConnectionStateListenable().addListener(csLister);
	}

    public void close() {
		logger.info("system resource release ...");
		this.release();
		logger.info("zookeeper close ...");
		zkClient.getZookeeperClient().close();
		zkClient.close();
    }

	/**
	 * 释放子类引入的外部资源
	 */
    public abstract void release();


	/**
	 * 获取zNode中的数据
	 * @param zNode
	 * @param tClass
	 * @param <T>
	 * @return
	 */
    public <T> T getZNodeData(String zNode,Class<T> tClass){
		try {
			String dataString = new String(zkClient.getData().forPath(zNode));
			return JSONUtils.parseObject(dataString,tClass);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}


	/**
	 *  heartbeat for zookeeper
	 * @param znode
	 */
	public void heartBeatForZk(String znode){
		try {

			if (Stopper.isStoped()){
				return;
			}

			//通过心跳机制解决worker节点漏监听自己的remove事件的情况
			if(zkClient.getState() == CuratorFrameworkState.STOPPED || checkIsDeadServer(znode)){
				stoppable.stop("i was judged to death, release resources and stop myself");
				return;
			}

			byte[] bytes = zkClient.getData().forPath(znode);
			String resInfoStr = new String(bytes);
			String[] splits = resInfoStr.split(Constants.COMMA);
			if (splits.length != Constants.HEARTBEAT_FOR_ZOOKEEPER_INFO_LENGTH){
				return;
			}
			String str = splits[0] + Constants.COMMA +splits[1] + Constants.COMMA
					+ OSUtils.cpuUsage() + Constants.COMMA
					+ OSUtils.memoryUsage() + Constants.COMMA
					+ splits[4] + Constants.COMMA
					+ DateUtils.dateToString(new Date());
			zkClient.setData().forPath(znode,str.getBytes());

		} catch (Exception e) {
			logger.error("heartbeat for zk failed : " + e.getMessage(), e);
			stoppable.stop("heartbeat for zk exception, release resources and stop myself");
		}
	}

	/**
	 *	check dead server or not , if dead, stop self
	 *
	 * @param zNode   		  node path
	 * @throws Exception
	 */
	protected boolean checkIsDeadServer(String zNode) throws Exception {
		return false;
	}



	/**
	 * create zookeeper path according the zk node type.
	 * @param zkNodeType
	 * @return
	 * @throws Exception
	 */
	private String createZNodePath(ZKNodeType zkNodeType) throws Exception {
		// specify the format of stored data in ZK nodes
		String heartbeatZKInfo = ResInfo.getHeartBeatInfo(new Date());
		// create temporary sequence nodes for master znode
		String parentPath = getZNodeParentPath(zkNodeType);
		String serverPathPrefix = parentPath + "/" + OSUtils.getHost();
		String registerPath = zkClient.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(
				serverPathPrefix + "_", heartbeatZKInfo.getBytes());
		logger.info("register {} node {} success" , zkNodeType.toString(), registerPath);
		return registerPath;
	}

	/**
	 * register server,  if server already exists, return null.
	 * @param zkNodeType
	 * @return register server path in zookeeper
	 */
	public String registerServer(ZKNodeType zkNodeType) throws Exception {
		String registerPath = null;
		String host = OSUtils.getHost();
		if(checkZKNodeExists(host, zkNodeType)){
			logger.error("{} server already started on host : {}, clear it" ,
					zkNodeType.toString(), host);
			deleteZKNodeExists(host,zkNodeType);
		}
		registerPath = createZNodePath(zkNodeType);
		this.isInit = true;
        logger.info("register {} node {} success", zkNodeType.toString(), registerPath);
		return registerPath;
	}





	/**
	 * for stop server
	 * @param serverStoppable
	 */
	public void setStoppable(IStoppable serverStoppable){
		this.stoppable = serverStoppable;
	}


	/**
	 *
	 * @return zookeeper quorum
	 */
	public  String getZookeeperQuorum(){
		StringBuilder sb = new StringBuilder();
		String[] zookeeperParamslist = zookeeperProperties.getQuorum().split(COMMA);
		for (String param : zookeeperParamslist) {
			sb.append(param).append(Constants.COMMA);
		}

		if(sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}



	/**
	 * get master server list map.
	 * result : {host : resource info}
	 * @return
	 */
	public Map<String, String> getServerMaps(ZKNodeType zkNodeType){

		Map<String, String> masterMap = new HashMap<>();
		try {
			String path =  getZNodeParentPath(zkNodeType);
			List<String> serverList  = getZkClient().getChildren().forPath(path);
			if (CollectionUtils.isNotEmpty(serverList)) {
				for (String server : serverList) {
					byte[] bytes = getZkClient().getData().forPath(path + "/" + server);
					String data = "";
					if (ArrayUtils.isNotEmpty(bytes)){
						data = new String(bytes);
					}
					masterMap.putIfAbsent(server, data);
				}
			}
		} catch (Exception e) {
			logger.error("get server list failed : " + e.getMessage(), e);
		}

		return masterMap;
	}

	/**
	 * check the zookeeper node already exists
	 * @param host
	 * @param zkNodeType
	 * @return
	 * @throws Exception
	 */
	public boolean checkZKNodeExists(String host, ZKNodeType zkNodeType) {
		String path = getZNodeParentPath(zkNodeType);
		if(StringUtils.isEmpty(path)){
			logger.error("check zk node exists error, host:{}, zk node type:{}",
					host, zkNodeType.toString());
			return false;
		}
		Map<String, String> serverMaps = getServerMaps(zkNodeType);
		for(String hostKey : serverMaps.keySet()){
			if(hostKey.startsWith(host)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 删除已存在的ZK节点
	 * @param host
	 * @param zkNodeType
	 * @return
	 */
	public void deleteZKNodeExists(String host, ZKNodeType zkNodeType) {
		String parentPath = getZNodeParentPath(zkNodeType);
		if(StringUtils.isEmpty(parentPath)){
			logger.error("check zk node exists error, host:{}, zk node type:{}",
					host, zkNodeType.toString());
		}
		try {
			List<String> workers = getZkClient().getChildren().forPath(parentPath);
			List<String> currentNodeWorkers = workers.stream()
					.filter(worker -> worker.contains(host))
					.collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(currentNodeWorkers)){
				getZkClient().delete().deletingChildrenIfNeeded().forPath(currentNodeWorkers.get(0));
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

    /**
     * 检查ZK上某个路径是否存在
     * @param zNodePath
     * @return
     */
	public boolean checkZNodeExists(String zNodePath){
        try {
            return getZkClient().checkExists().forPath(zNodePath) != null;
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return false;
    }

	/**
	 *  get zkclient
	 * @return
	 */
	public  CuratorFramework getZkClient() {
		return zkClient;
	}

	/**
	 * get worker node parent path
	 * @return
	 */
	protected String getWorkerZNodeParentPath(){return zookeeperProperties.getWorkerParentZNode();};


    /**
     * get task instance parent path
     * @return
     */
    protected String getTaskZNodeParentPath(){
    	String taskBasePath = zookeeperProperties.getInstanceParentZNode();
		Assert.notNull(taskBasePath,"task instance base path can not empty");
		String host = OSUtils.getHost();
		Assert.notNull(taskBasePath,"Get worker Ip failed ");
    	return String.format("%s/%s",taskBasePath,host);
    }



	/**
	 * get zookeeper node parent path
	 * @param zkNodeType
	 * @return
	 */
	public String getZNodeParentPath(ZKNodeType zkNodeType) {
		String path = "";
		switch (zkNodeType){
			case WORKER:
				return getWorkerZNodeParentPath();
            case TASK_INSTANCE:
                return getTaskZNodeParentPath();
			default:
				break;
		}
		return path;
	}


	/**
	 *  init system znode
	 */
	protected void initSystemZNode(){
		try {
			createNodePath(getTaskZNodeParentPath());
			createNodePath(getWorkerZNodeParentPath());

		} catch (Exception e) {
			logger.error("init system znode failed : " + e.getMessage(),e);
		}
	}

	/**
	 * create zookeeper node path if not exists
	 * @param zNodeParentPath
	 * @throws Exception
	 */
	private void createNodePath(String zNodeParentPath) throws Exception {
	    if(null == zkClient.checkExists().forPath(zNodeParentPath)){
	        zkClient.create().creatingParentContainersIfNeeded()
					.withMode(CreateMode.PERSISTENT).forPath(zNodeParentPath);
		}
	}

	/**
	 * server self dead, stop all threads
	 * @param serverHost
	 * @param zkNodeType
	 */
	protected boolean checkServerSelfDead(String serverHost, ZKNodeType zkNodeType) {
		if (serverHost.equals(OSUtils.getHost()) && this.isInit) {
			logger.error("{} server({}) of myself dead , stopping...",
					zkNodeType.toString(), serverHost);
			stoppable.stop(String.format(" %s server %s of myself dead , stopping...",
					zkNodeType.toString(), serverHost));
			return true;
		}
		return false;
	}

	/**
	 *  get host ip, string format: masterParentPath/ip_000001/value
	 * @param path
	 * @return
	 */
	protected String getHostByEventDataPath(String path) {
		int  startIndex = path.lastIndexOf("/")+1;
		int endIndex = 	path.lastIndexOf("_");

		if(startIndex >= endIndex){
			logger.error("parse ip error");
			return "";
		}
		return path.substring(startIndex, endIndex);
	}

	@Override
	public String toString() {
		return "AbstractZKClient{" +
				"zkClient=" + zkClient +
				", masterZNodeParentPath='" + getZNodeParentPath(ZKNodeType.MASTER) + '\'' +
				", workerZNodeParentPath='" + getZNodeParentPath(ZKNodeType.WORKER) + '\'' +
				", stoppable=" + stoppable +
				'}';
	}

	/**
	 * 删除zNode
	 * @param zNodePath
	 */
	protected void deleteZNode(String zNodePath){
		try {
			this.zkClient.delete().forPath(zNodePath);
		} catch (Exception e) {
			logger.error("删除zNode{}失败：{}",zNodePath,e.getMessage());
		}
	}

}

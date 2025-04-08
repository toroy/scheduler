package com.clubfactory.platform.scheduler.engine.task;

import com.clubfactory.platform.scheduler.core.exception.BuildTaskFailedException;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.dal.po.JobType;
import com.clubfactory.platform.scheduler.engine.utils.PluginLoadUtil;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author xiejiajun
 */
public class TaskManager {

    /**
     * @param taskVO
     * @param logger
     * @return
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public static AbstractTask newTask(TaskVO taskVO, Logger logger, AbstractTask.StateTracker stateTracker) {
        JobType jobType = taskVO.getJobTypeInfo();
        return PluginLoadUtil.newInstance(jobType.getPluginDir(),jobType.getPluginName(),cl -> {
            String clazzName = taskVO.isRunOnTmpEmr() ? jobType.getPluginEmrClazz() : jobType.getPluginClazz();
            if (taskVO.isRunOnTmpEmr() && StringUtils.isBlank(clazzName)) {
                throw new RuntimeException(jobType.getType() + "作业暂不支持临时集群");
            }
            Class<? extends AbstractTask> clazz = (Class<? extends AbstractTask>)cl.loadClass(clazzName);
            if (clazz == null) {
                logger.error("the task type: {} is not supported", jobType.getPluginName());
                throw new IllegalArgumentException("not support task type");
            }
            try {
                Constructor<? extends AbstractTask> constructor = clazz.getConstructor(TaskVO.class, Logger.class,
                        AbstractTask.StateTracker.class);
                constructor.setAccessible(true);
                return constructor.newInstance(taskVO, logger, stateTracker);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new BuildTaskFailedException(e.getMessage(), e);
            }
        });
    }

}

package com.clubfactory.platform.scheduler.engine.config;

/**
 * @author xiejiajun
 */
public interface ZkConstant {
	/**
	 * 默认节点路径
	 */
	String ROOT_PATH = "/platform/scheduler";
	
	/**
	 * 工作节点路径
	 */
	String TASK_PATH = ROOT_PATH + "/task";

	/**
	 * worker节点zNode
	 */
	String DEFAULT_WORKER_PATH = ROOT_PATH + "/workers";
	
}

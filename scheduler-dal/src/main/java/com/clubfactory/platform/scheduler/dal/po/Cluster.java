package com.clubfactory.platform.scheduler.dal.po;


import java.util.List;

import com.clubfactory.platform.scheduler.dal.enums.ClusterTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.CommonStatus;

import lombok.Data;

@Data
public class Cluster extends BasePO {

	/**
	 * 集群url
	 */
	private String url;
	
	/**
	 * 执行用户名
	 */
	private String proxyUser;

	/**
	 * 执行用户密码
	 */
	private String proxyPassword;
	
	/**
	 * 功能，多个逗号分隔
	 */
	private String functions;
	
	/**
	 * 状态
	 */
	private CommonStatus status;
	
	/**
	 * 团队ID
	 */
	private Integer departId;
	
	/**
	 * 集群类型
	 */
	private ClusterTypeEnum type;

	/**
	 * 集群名字
	 */
	private String clusterName;

	/**
	 * 用于支持按集群名称模糊查询的字段，不会入库，只做查询条件
	 */
	private List<Integer> departIds;


	/**
	 * 集群对应的Yarn RM hosts（可为IP，多个用英文逗号分隔）
	 */
	private String yarnRMHosts;

	/**
	 * Yarn Http端口
	 */
	private String yarnRMHttpPort;

	/**
	 * yarn超级用户
	 */
	private String yarnSuperUser;

}

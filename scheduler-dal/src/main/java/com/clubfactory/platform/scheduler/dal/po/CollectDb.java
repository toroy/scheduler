package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.CommonStatus;
import com.clubfactory.platform.scheduler.dal.enums.DbFeatureEnum;
import com.clubfactory.platform.scheduler.dal.enums.DbType;

import lombok.Data;

@Data
public class CollectDb extends BasePO {

	/**
	 * 名称
	 */
	private String dsName;

	/**
	 * 类型
	 */
	private DbType dsType;
	
	/**
	 * 用户名
	 */
	private String dsUser;
	
	/**
	 * 密码
	 */
	private String dsPassword;
	
	/**
	 * 库url
	 */
	private String dsUrl;

    /**
     * 数据源对应的库名
     */
    private String dbName;

	/**
	 * 数据源对应的数据库服务器host
	 */
    private String dbHost;

	/**
	 * 数据源对应的数据库服务端口
	 */
    private String dbPort;
	
	/**
	 * 功能
	 */
	private DbFeatureEnum feature;
	
	/**
	 * 状态
	 */
	private CommonStatus status;
	
	/**
	 * 参数
	 */
	private String param;

	/**
	 * 密码加密密钥
	 */
	private String pwdKey;


	/**
	 * 加密后的密码：用于替换原来的dsPassword
	 */
	private String encryptPwd;
	
}

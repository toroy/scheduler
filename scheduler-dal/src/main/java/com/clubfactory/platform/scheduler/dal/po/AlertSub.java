package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.SubscribeType;
import lombok.Data;

/**
 * 使用createUser标识该订阅信息owner
 * @author xiejiajun
 */
@Data
public class AlertSub extends BasePO {

	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 库名
	 */
	private String dbName;
	
	/**
	 * 数据源
	 */
	private String dataSource;
	
	/**
	 * 表责任人ID 对应sc_user中的uid
	 */
	private String picId;
	
	/**
	 * 订阅方式 依赖订阅，主动订阅
	 */
	private SubscribeType subType;
}

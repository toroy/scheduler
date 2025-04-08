package com.clubfactory.platform.scheduler.dal.po;

import com.clubfactory.platform.scheduler.dal.enums.ScriptType;
import lombok.Data;

@Data
public class Script extends BasePO {


	/**
	 * 描述
	 */
	private String remark;

	/**
	 * 任务资源名称（UUID)
	 */
	private String fileName;

	/**
	 * 脚本类型
	 */
	private ScriptType scriptType;
	
	/**
	 * 版本号
	 */
	private Integer version;

	/**
	 * 文件扩展名，用于判断文件是否可在线阅览编辑
	 */
	private String fileExt;

	/**
	 * 用户定义的文件名称
	 */
	private String scriptName;

	/**
	 * 脚本存储父路径
	 */
	private String scriptBasePath;
}

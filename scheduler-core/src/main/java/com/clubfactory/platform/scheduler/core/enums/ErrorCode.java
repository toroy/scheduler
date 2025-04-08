package com.clubfactory.platform.scheduler.core.enums;

import com.clubfactory.platform.common.constant.IErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

public enum ErrorCode implements IErrorCode {

	CLIENT_ADD_DATA_ERROR(11000, "创建data出错"),
	START_CURATOR_ERROR(11001, "启动curtor出错"),
	DB_CONN_ERROR(11002, "%s");
	
	private Integer errorCode;
    private String errorMsg;
    private String[] params;
    
    ErrorCode(Integer errorCode, String errorMsg, String... params) {
    	this.errorCode = errorCode;
    	this.errorMsg = errorMsg;
    	this.params = params;
    }
    
    @Override
    public String getErrorMsg() {
    	if (params != null && params.length != 0 && errorMsg.contains("%s")) {
    		try {
				return String.format(errorMsg, params).replace("%s", "");
			} catch (Exception e) {
			}
    	}
		return errorMsg;
    }
    
    public ErrorCode setParams(String... params) {
    	this.params = params;
    	return this;
    }

	@Override
	public Integer getErrorCode() {
		return this.errorCode;
	}

}

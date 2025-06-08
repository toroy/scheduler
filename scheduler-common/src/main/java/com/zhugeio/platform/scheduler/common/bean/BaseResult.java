package com.zhugeio.platform.scheduler.common.bean;


import com.zhugeio.platform.scheduler.common.constant.IErrorCode;

import java.io.Serializable;


/**
 */
public class BaseResult<T> implements Serializable {

	private static final long serialVersionUID = 4998958908867934913L;

    /**
     * 信息反馈
     */
	public static final int SUCCESS = 0;
	public static final int FALIED = 1;
    public static final String SUCCESS_MSG = "操作成功!";
    public static final String FAILED_MSG = "操作失败!";
    protected int code;
    protected String message;
    protected T body;
    protected Boolean success;
    
    /**
	 * @return success
	 */
	public Boolean isSuccess() {
		return success;
	}

	/**
	 * @param other the other to set
	 */
	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public BaseResult() {
        this.success = true;
        this.code = 0;
        this.message = SUCCESS_MSG;
    }

    public BaseResult(T body) {
        this.message = SUCCESS_MSG;
        this.success = true;
        this.code = 0;
        this.body = body;
    }
    
    public BaseResult(int code, String message) {
        this.code = code;
        this.message = message;
        this.success = code == 0 ? true : false;
    }
    
    public BaseResult(IErrorCode code) {
        this.code = code.getErrorCode();
        this.message = code.getErrorMsg();
        this.success = false;
    }

    public BaseResult(int code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
        this.success = code == 0 ? true : false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}

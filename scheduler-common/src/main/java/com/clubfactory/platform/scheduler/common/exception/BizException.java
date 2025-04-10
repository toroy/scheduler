package com.clubfactory.platform.scheduler.common.exception;


import com.clubfactory.platform.scheduler.common.constant.IErrorCode;

public class BizException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final int errorCode;

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = code;
    }

    public BizException(String message) {
        this(1, message, (Throwable)null);
    }

    public BizException(IErrorCode errorCode) {
        this(errorCode.getErrorCode(), errorCode.getErrorMsg(), (Throwable)null);
    }

    public BizException(IErrorCode errorCode, Throwable cause) {
        this(errorCode.getErrorCode(), errorCode.getErrorMsg(), cause);
    }

    public BizException(int code, String message) {
        this(code, message, (Throwable)null);
    }

    public BizException(int code, Throwable cause) {
        this(code, (String)null, cause);
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}

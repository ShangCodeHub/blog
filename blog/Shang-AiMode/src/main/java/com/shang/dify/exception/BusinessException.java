package com.shang.dify.exception;

import com.shang.dify.common.vo.ResultCode;

/**
 * 业务异常类
 * 用于替代 com.sciz.server.infrastructure.shared.exception.BusinessException
 */
public class BusinessException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private ResultCode resultCode;
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }
    
    public BusinessException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }



    public ResultCode getResultCode() {
        return resultCode;
    }
    
    /**
     * 静态工厂方法，用于创建业务异常
     */
    public static BusinessException of(ResultCode resultCode, String message, Object... args) {
        String formattedMessage = String.format(message, args);
        return new BusinessException(resultCode, formattedMessage);
    }
    
    public static BusinessException of(ResultCode resultCode, String message) {
        return new BusinessException(resultCode, message);
    }
}

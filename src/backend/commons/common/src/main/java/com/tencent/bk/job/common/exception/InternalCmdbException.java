package com.tencent.bk.job.common.exception;

import lombok.Getter;
import lombok.ToString;

/**
 * 内部服务异常--调用CMDB异常
 */
@Getter
@ToString
public class InternalCmdbException extends InternalException {

    public InternalCmdbException(String internalErrorMessage, Throwable cause) {
        super(internalErrorMessage, cause);
    }

    public InternalCmdbException(String internalErrorMessage) {
        super(internalErrorMessage);
    }
}

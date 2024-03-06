package com.tencent.bk.job.common.exception;

import lombok.Getter;
import lombok.ToString;

/**
 * 内部服务异常--调用cmsi接口异常
 */
@Getter
@ToString
public class InternalCmsiException extends InternalException {

    public InternalCmsiException(String internalErrorMessage, Throwable cause) {
        super(internalErrorMessage, cause);
    }

    public InternalCmsiException(String internalErrorMessage) {
        super(internalErrorMessage);
    }
}

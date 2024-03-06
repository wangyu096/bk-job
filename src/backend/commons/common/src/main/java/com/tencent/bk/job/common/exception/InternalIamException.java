package com.tencent.bk.job.common.exception;

import lombok.Getter;
import lombok.ToString;

/**
 * 内部服务异常--调用IAM异常
 */
@Getter
@ToString
public class InternalIamException extends InternalException {

    public InternalIamException(String internalErrorMessage, Throwable cause) {
        super(internalErrorMessage, cause);
    }

    public InternalIamException(String internalErrorMessage) {
        super(internalErrorMessage);
    }
}

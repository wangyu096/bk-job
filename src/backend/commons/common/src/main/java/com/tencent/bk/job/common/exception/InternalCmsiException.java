package com.tencent.bk.job.common.exception;

import com.tencent.bk.job.common.error.ErrorReason;
import com.tencent.bk.job.common.error.payload.ErrorInfoPayloadDTO;
import com.tencent.bk.job.common.exception.base.InternalException;
import lombok.Getter;
import lombok.ToString;

/**
 * 内部服务异常--调用cmsi接口异常
 */
@Getter
@ToString
public class InternalCmsiException extends InternalException {

    public InternalCmsiException(String message,
                                 Throwable cause) {
        super(message, cause, new ErrorInfoPayloadDTO("bk_cmsi", ErrorReason.REQUEST_THIRD_API_ERROR));
    }

    public InternalCmsiException(String message) {
        super(message, new ErrorInfoPayloadDTO("bk_cmsi", ErrorReason.REQUEST_THIRD_API_ERROR));
    }
}

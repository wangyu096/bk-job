package com.tencent.bk.job.common.exception;

import com.tencent.bk.job.common.error.ErrorReason;
import com.tencent.bk.job.common.error.payload.ErrorInfoPayloadDTO;
import com.tencent.bk.job.common.exception.base.InternalException;
import lombok.Getter;
import lombok.ToString;

/**
 * 内部服务异常--调用CMDB异常
 */
@Getter
@ToString
public class InternalCmdbException extends InternalException {

    public InternalCmdbException(String message,
                                 Throwable cause) {
        super(message, cause, new ErrorInfoPayloadDTO("bk_cmdb", ErrorReason.REQUEST_THIRD_API_ERROR));
    }

    public InternalCmdbException(String message) {
        super(message, new ErrorInfoPayloadDTO("bk_cmdb", ErrorReason.REQUEST_THIRD_API_ERROR));
    }
}

package com.tencent.bk.job.file.worker.cos;

import com.tencent.bk.job.common.error.ErrorReason;
import com.tencent.bk.job.common.error.payload.ErrorInfoPayloadDTO;
import com.tencent.bk.job.common.exception.base.InternalException;
import lombok.Getter;
import lombok.ToString;

/**
 * 内部服务异常--调用腾讯云 COS 异常
 */
@Getter
@ToString
public class CosApiException extends InternalException {

    public CosApiException(String message,
                           Throwable cause) {
        super(message, cause, new ErrorInfoPayloadDTO("tencent_cos", ErrorReason.REQUEST_THIRD_API_ERROR));
    }

    public CosApiException(String message) {
        super(message, new ErrorInfoPayloadDTO("tencent_cos", ErrorReason.REQUEST_THIRD_API_ERROR));
    }
}

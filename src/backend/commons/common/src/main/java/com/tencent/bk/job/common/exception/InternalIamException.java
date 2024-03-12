package com.tencent.bk.job.common.exception;

import com.tencent.bk.job.common.error.ErrorReason;
import com.tencent.bk.job.common.error.payload.ErrorInfoPayloadDTO;
import com.tencent.bk.job.common.exception.base.InternalException;
import lombok.Getter;
import lombok.ToString;

/**
 * 内部服务异常--调用IAM异常
 */
@Getter
@ToString
public class InternalIamException extends InternalException {

//    public InternalIamException(String message, SubErrorCode subErrorCode) {
//        super(message, subErrorCode);
//    }
//
//    public InternalIamException(String message,
//                                Throwable cause,
//                                SubErrorCode subErrorCode) {
//        super(message, cause, subErrorCode);
//    }
//
//    public InternalIamException(Throwable cause) {
//        super(cause);
//    }

//    public InternalIamException(Throwable cause, ErrorInfoPayloadDTO payload) {
//        super(cause, payload);
//    }

    public InternalIamException(String message,
                                Throwable cause) {
        super(message, cause, new ErrorInfoPayloadDTO("bk_iam", ErrorReason.REQUEST_THIRD_API_ERROR));
    }

    public InternalIamException(String message) {
        super(message, new ErrorInfoPayloadDTO("bk_iam", ErrorReason.REQUEST_THIRD_API_ERROR));
    }

    public InternalIamException(Throwable cause) {
        super(cause, new ErrorInfoPayloadDTO("bk_iam", ErrorReason.REQUEST_THIRD_API_ERROR));
    }
}

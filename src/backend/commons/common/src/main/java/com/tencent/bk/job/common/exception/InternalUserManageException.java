package com.tencent.bk.job.common.exception;

import com.tencent.bk.job.common.error.ErrorReason;
import com.tencent.bk.job.common.error.payload.ErrorInfoPayloadDTO;
import com.tencent.bk.job.common.exception.base.InternalException;
import lombok.Getter;
import lombok.ToString;

/**
 * 内部服务异常--用户管理异常
 */
@Getter
@ToString
public class InternalUserManageException extends InternalException {

    public InternalUserManageException(String message,
                                       Throwable cause) {
        super(message, cause, new ErrorInfoPayloadDTO("bk_user_management",
            ErrorReason.REQUEST_THIRD_API_ERROR));
    }

    public InternalUserManageException(String message) {
        super(message, new ErrorInfoPayloadDTO("bk_user_management", ErrorReason.REQUEST_THIRD_API_ERROR));
    }
}

package com.tencent.bk.job.common.notice.exception;

import com.tencent.bk.job.common.error.SubErrorCode;
import com.tencent.bk.job.common.error.payload.ErrorPayloadDTO;
import com.tencent.bk.job.common.exception.base.InternalException;
import lombok.Getter;
import lombok.ToString;

/**
 * 调用蓝鲸消息通知中心异常
 */
@Getter
@ToString
public class BkNoticeException extends InternalException {

    public BkNoticeException(String message, SubErrorCode subErrorCode) {
        super(message, subErrorCode);
    }

    public BkNoticeException(String message, Throwable cause, ErrorPayloadDTO payload) {
        super(message, cause, payload);
    }
}

package com.tencent.bk.job.common.exception;

import com.tencent.bk.job.common.exception.base.InternalException;
import lombok.Getter;
import lombok.ToString;

/**
 * 内部服务异常--配置异常
 */
@Getter
@ToString
public class IncorrectConfigException extends InternalException {

    public IncorrectConfigException(String message) {
        super(message);
    }
}

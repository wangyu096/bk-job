package com.tencent.bk.job.crontab.api.common;

import com.tencent.bk.job.common.exception.base.InvalidParamException;
import com.tencent.bk.job.crontab.util.CronExpressionUtil;
import org.quartz.CronExpression;

public class CronCheckUtil {

    public static void checkCronExpression(String cronExpression, String paramName) {
        // 校验cron表达式的有效性
        try {
            cronExpression = CronExpressionUtil.fixExpressionForQuartz(cronExpression);
            new CronExpression(cronExpression);
        } catch (Exception e) {

            throw InvalidParamException.withInvalidField(paramName,
                "cronExpression is invalid, should be 5-char Linux cron, like 0/5 * * * *(? not supported)"
            );
        }
    }

}

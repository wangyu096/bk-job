package com.tencent.bk.job.execute.service.impl;

import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.error.SubErrorCode;
import com.tencent.bk.job.common.exception.base.InvalidParamException;
import com.tencent.bk.job.common.exception.base.NotFoundException;
import com.tencent.bk.job.execute.service.StepInstanceService;
import com.tencent.bk.job.execute.service.StepInstanceValidateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StepInstanceValidateServiceImpl implements StepInstanceValidateService {

    private final StepInstanceService stepInstanceService;

    public StepInstanceValidateServiceImpl(StepInstanceService stepInstanceService) {
        this.stepInstanceService = stepInstanceService;
    }

    @Override
    public void checkStepInstance(long appId, Long taskInstanceId, Long stepInstanceId) {
        // 检查taskInstanceId与stepInstanceId关联关系的正确性
        Long realTaskInstanceId = stepInstanceService.getStepTaskInstanceId(appId, stepInstanceId);
        if (realTaskInstanceId == null) {
            throw new NotFoundException(SubErrorCode.of(ErrorCode.STEP_INSTANCE_NOT_EXIST));
        } else if (!realTaskInstanceId.equals(taskInstanceId)) {
            log.info("stepInstance {} does not belong to taskInstance {}", stepInstanceId, taskInstanceId);
            throw InvalidParamException.withInvalidField("job_instance_id");
        }
    }
}

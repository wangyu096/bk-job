package com.tencent.bk.job.execute.service;

public interface StepInstanceValidateService {

    void checkStepInstance(long appId, Long taskInstanceId, Long stepInstanceId);

}

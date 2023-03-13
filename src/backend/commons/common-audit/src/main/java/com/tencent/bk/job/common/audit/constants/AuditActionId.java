package com.tencent.bk.job.common.audit.constants;

import com.tencent.bk.job.common.iam.constant.ActionId;

public enum AuditActionId {
    EXECUTE_SCRIPT("execute_script", ActionId.EXECUTE_SCRIPT);

    /**
     * id
     */
    private final String id;
    /**
     * IAM action id
     */
    private final String iamActionId;

    AuditActionId(String id, String iamActionId) {
        this.id = id;
        this.iamActionId = iamActionId;
    }

}

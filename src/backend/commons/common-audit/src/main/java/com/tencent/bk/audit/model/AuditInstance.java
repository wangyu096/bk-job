package com.tencent.bk.audit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.bk.audit.constants.Constants;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 审计对象
 */
@Data
@NoArgsConstructor
public class AuditInstance {
    /**
     * 实例ID
     */
    @JsonProperty("event_id")
    private String id;

    /**
     * 实例名
     */
    @JsonProperty("instance_name")
    private String instanceName;

    /**
     * 实例风险等级
     */
    @JsonProperty("instance_sensitivity")
    private int instanceSensitivity = Constants.DEFAULT_SENSITIVITY;

    /**
     * 资源实例数据
     */
    @JsonProperty("instance_data")
    private Map<String, String> instanceData;

    /**
     * 资源实例原始数据
     */
    @JsonProperty("instance_origin_data")
    private Map<String, String> instanceOriginData;

    public AuditInstance(String id, String instanceName) {
        this.id = id;
        this.instanceName = instanceName;
    }
}

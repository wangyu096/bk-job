package com.tencent.bk.job.common.audit.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 审计中心配置
 */
@ConfigurationProperties(prefix = "auditEntry")
@Getter
@Setter
@ToString
public class AuditProperties {
    private boolean enabled;

    /**
     * 审计事件 Exporter 配置
     */
    private Exporter exporter;

    @Getter
    @Setter
    @ToString
    private static class Exporter {
        private String type;
    }
}

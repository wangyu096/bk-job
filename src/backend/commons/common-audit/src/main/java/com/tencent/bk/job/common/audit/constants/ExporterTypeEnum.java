package com.tencent.bk.job.common.audit.constants;

/**
 * 审计事件 Exporter 类型
 */
public enum ExporterTypeEnum {
    /**
     * 审计日志文件
     */
    LOG_FILE("log_file"),
    /**
     * OpenTelemetry log
     */
    OTEL("otel_log");


    ExporterTypeEnum(String type) {
        this.type = type;
    }

    private final String type;

    public String getValue() {
        return type;
    }
}

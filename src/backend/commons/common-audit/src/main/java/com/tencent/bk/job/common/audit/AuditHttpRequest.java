package com.tencent.bk.job.common.audit;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuditHttpRequest {
    private String uri;
    private String queryParams;
    private Object body;

    public AuditHttpRequest(String uri, String queryParams, Object body) {
        this.uri = uri;
        this.queryParams = queryParams;
        this.body = body;
    }
}

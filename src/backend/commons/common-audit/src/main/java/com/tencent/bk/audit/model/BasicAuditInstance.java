package com.tencent.bk.audit.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicAuditInstance {
    private String id;
    private String name;

    public BasicAuditInstance(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

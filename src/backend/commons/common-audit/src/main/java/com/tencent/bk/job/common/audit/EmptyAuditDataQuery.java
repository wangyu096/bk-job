package com.tencent.bk.job.common.audit;

public class EmptyAuditDataQuery<T, R> implements AuditDataQuery<T, R> {
    public static final EmptyAuditDataQuery INSTANCE = new EmptyAuditDataQuery<>();

    @Override
    public R apply(T id) {
        return null;
    }
}

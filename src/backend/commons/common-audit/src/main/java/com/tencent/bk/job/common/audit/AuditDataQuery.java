package com.tencent.bk.job.common.audit;

@FunctionalInterface
public interface AuditDataQuery<T, R> {

    R apply(T id);
}

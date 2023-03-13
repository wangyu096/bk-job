package com.tencent.bk.job.common.audit.utils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AuditInstanceUtils {
    public static <T> String extract(List<T> instanceList, Function<T, String> f) {
        return instanceList.stream().map(f).collect(Collectors.joining(","));
    }
}

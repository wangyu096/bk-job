/*
 * Tencent is pleased to support the open source community by making BK-JOB蓝鲸智云作业平台 available.
 *
 * Copyright (C) 2021 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-JOB蓝鲸智云作业平台 is licensed under the MIT License.
 *
 * License for BK-JOB蓝鲸智云作业平台:
 * --------------------------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.tencent.bk.audit.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 时间工具类
 */
@Slf4j
public class DateTimeUtils {

    /**
     * 格式化当前时间
     *
     * @param formatter DateTimeFormatter
     * @return 格式化之后的时间
     */
    public static String formatCurrentTimestamp(DateTimeFormatter formatter) {
        return formatUnixTimestamp(System.currentTimeMillis(), ChronoUnit.MILLIS, formatter);
    }

    /**
     * 格式化时间戳
     *
     * @param unixTimestamp UNIX 时间戳
     * @param unit          时间单位
     * @param zone          时区,如果不传默认使用系统时区
     * @param formatter     DateTimeFormatter
     * @return 格式化之后的时间
     */
    public static String formatUnixTimestamp(long unixTimestamp,
                                             ChronoUnit unit,
                                             ZoneId zone,
                                             DateTimeFormatter formatter) {
        return parseUnixTimestamp(unixTimestamp, unit, zone).format(formatter);
    }

    /**
     * 格式化时间戳。使用系统当前时区
     *
     * @param unixTimestamp UNIX 时间戳
     * @param unit          时间单位
     * @return 格式化之后的时间
     */
    public static String formatUnixTimestamp(long unixTimestamp, ChronoUnit unit, DateTimeFormatter formatter) {
        return formatUnixTimestamp(unixTimestamp, unit, ZoneId.systemDefault(), formatter);
    }

    private static ZonedDateTime parseUnixTimestamp(long unixTimestamp, ChronoUnit unit, ZoneId zone) {
        if (unit != null && (unit != ChronoUnit.SECONDS && unit != ChronoUnit.MILLIS)) {
            throw new UnsupportedOperationException("Unsupported conversion with unit:" + unit.name());
        }
        Instant instant;
        unit = (unit == null ? ChronoUnit.SECONDS : unit);
        if (unit == ChronoUnit.MILLIS) {
            instant = Instant.ofEpochMilli(unixTimestamp);
        } else {
            instant = Instant.ofEpochSecond(unixTimestamp);
        }
        ZonedDateTime dateTime;
        if (zone != null) {
            dateTime = ZonedDateTime.ofInstant(instant, zone);
        } else {
            dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        }
        return dateTime;
    }

}

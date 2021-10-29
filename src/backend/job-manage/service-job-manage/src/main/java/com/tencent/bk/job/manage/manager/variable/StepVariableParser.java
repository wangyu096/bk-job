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

package com.tencent.bk.job.manage.manager.variable;

import com.tencent.bk.job.manage.model.dto.task.TaskStepDTO;
import com.tencent.bk.job.manage.model.dto.task.TaskVariableDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StepVariableParser {

    public static List<Pair<TaskStepDTO, List<TaskVariableDTO>>> parse(List<TaskStepDTO> steps,
                                                                       List<TaskVariableDTO> variables) {
        return null;
    }

    /**
     * 使用job的标准格式解析变量
     *
     * @param content 被解析的内容
     * @return 变量列表
     */
    public static List<String> parseJobStandardVar(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }

        Matcher m = Pattern.compile("\\$\\{([_a-zA-Z][0-9_a-zA-Z]*)}").matcher(content);
        List<String> varNames = new ArrayList<>();
        while (m.find()) {
            String varName = m.group(1);
            if (!varNames.contains(varName)) {
                varNames.add(varName);
            }
        }
        return varNames;
    }

    /**
     * 从shell脚本解析变量
     *
     * @param shellScriptContent shell脚本内容
     * @return 变量列表
     */
    public static List<String> parseShellScriptVar(String shellScriptContent) {
        String content = filterCommentLine(shellScriptContent);
        Matcher m = Pattern.compile("\\$\\{[#!]?([_a-zA-Z][0-9_a-zA-Z]*)\\S*}").matcher(content);
        List<String> varNames = new ArrayList<>();
        while (m.find()) {
            String varName = m.group(1);
            if (!varNames.contains(varName)) {
                varNames.add(varName);
            }
        }
        return varNames;
    }

    private static String filterCommentLine(String shellScriptContent) {
        String[] lines = shellScriptContent.split("\n");
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            String trimLine = line.trim();
            if (StringUtils.isBlank(trimLine) || trimLine.startsWith("#")) {
                continue;
            }
            builder.append(line).append("\n");
        }
        return builder.toString();
    }
}

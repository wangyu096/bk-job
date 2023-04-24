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

package com.tencent.bk.audit.model;

import java.util.List;
import java.util.Map;

public class InvalidActionAuditContext implements ActionAuditContext {
    @Override
    public ActionAuditScope makeCurrent() {
        return new NoopActionAuditScope();
    }

    @Override
    public ActionAuditContext addAttribute(String name, Object value) {
        return this;
    }


    @Override
    public void end() {

    }

    @Override
    public String getActionId() {
        return null;
    }

    @Override
    public Long getStartTime() {
        return null;
    }

    @Override
    public Long getEndTime() {
        return null;
    }

    @Override
    public String getResourceType() {
        return null;
    }

    @Override
    public List<String> getInstanceIdList() {
        return null;
    }

    @Override
    public List<String> getInstanceNameList() {
        return null;
    }

    @Override
    public List<Object> getOriginInstanceList() {
        return null;
    }

    @Override
    public List<Object> getInstanceList() {
        return null;
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public List<AuditEvent> getEvents() {
        return null;
    }

    @Override
    public ActionAuditContext setInstanceIdList(List<String> instanceIdList) {
        return this;
    }

    @Override
    public ActionAuditContext setInstanceNameList(List<String> instanceNameList) {
        return this;
    }

    @Override
    public ActionAuditContext setOriginInstanceList(List<Object> originInstanceList) {
        return this;
    }

    @Override
    public ActionAuditContext setInstanceList(List<Object> instanceList) {
        return this;
    }

    @Override
    public ActionAuditContext setInstanceId(String instanceId) {
        return this;
    }

    @Override
    public ActionAuditContext setInstanceName(String instanceName) {
        return this;
    }

    @Override
    public ActionAuditContext setOriginInstance(Object originInstance) {
        return this;
    }

    @Override
    public ActionAuditContext setInstance(Object instance) {
        return this;
    }
}

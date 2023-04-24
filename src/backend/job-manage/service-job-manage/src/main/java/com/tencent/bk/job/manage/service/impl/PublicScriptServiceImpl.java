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

package com.tencent.bk.job.manage.service.impl;

import com.tencent.bk.job.common.model.PageData;
import com.tencent.bk.job.manage.model.dto.ScriptBasicDTO;
import com.tencent.bk.job.manage.model.dto.ScriptDTO;
import com.tencent.bk.job.manage.model.dto.SyncScriptResultDTO;
import com.tencent.bk.job.manage.model.dto.TagDTO;
import com.tencent.bk.job.manage.model.dto.TemplateStepIDDTO;
import com.tencent.bk.job.manage.model.query.ScriptQuery;
import com.tencent.bk.job.manage.model.web.vo.TagCountVO;
import com.tencent.bk.job.manage.service.PublicScriptService;
import com.tencent.bk.job.manage.service.ScriptManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.tencent.bk.job.common.constant.JobConstants.PUBLIC_APP_ID;

/**
 * 公共脚本Service
 */
@Slf4j
@Service
public class PublicScriptServiceImpl implements PublicScriptService {
    private final ScriptManager scriptManager;

    @Autowired
    public PublicScriptServiceImpl(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    @Override
    public PageData<ScriptDTO> listPageScript(ScriptQuery scriptCondition) {
        return scriptManager.listPageScript(scriptCondition);
    }

    @Override
    public List<ScriptDTO> listScripts(ScriptQuery scriptQuery) {
        return scriptManager.listScripts(scriptQuery);
    }

    @Override
    public ScriptDTO getScript(String scriptId) {
        return scriptManager.getScript(PUBLIC_APP_ID, scriptId);
    }

    @Override
    public List<ScriptBasicDTO> listScriptBasicInfoByScriptIds(Collection<String> scriptIds) {
        return scriptManager.listScriptBasicInfoByScriptIds(scriptIds);
    }

    @Override
    public ScriptDTO saveScript(ScriptDTO script) {
        return scriptManager.saveScript(script);
    }

    @Override
    public void deleteScript(String scriptId) {
        scriptManager.deleteScript(PUBLIC_APP_ID, scriptId);
    }

    @Override
    public ScriptDTO getScriptVersion(Long scriptVersionId) {
        return scriptManager.getScriptVersion(PUBLIC_APP_ID, scriptVersionId);
    }

    @Override
    public List<ScriptDTO> listScriptVersion(String scriptId) {
        return scriptManager.listScriptVersion(PUBLIC_APP_ID, scriptId);
    }

    @Override
    public ScriptDTO saveScriptVersion(ScriptDTO scriptVersion) {
        return scriptManager.saveScriptVersion(scriptVersion);
    }

    @Override
    public ScriptDTO updateScriptVersion(ScriptDTO scriptVersion) {
        return scriptManager.updateScriptVersion(scriptVersion);
    }

    @Override
    public void deleteScriptVersion(Long scriptVersionId) {
        scriptManager.deleteScriptVersion(PUBLIC_APP_ID, scriptVersionId);
    }

    @Override
    public void publishScript(String scriptId, Long scriptVersionId) {
        scriptManager.publishScript(PUBLIC_APP_ID, scriptId, scriptVersionId);
    }

    @Override
    public void disableScript(String scriptId, Long scriptVersionId) {
        scriptManager.disableScript(PUBLIC_APP_ID, scriptId, scriptVersionId);
    }

    @Override
    public Map<String, ScriptDTO> batchGetOnlineScriptVersionByScriptIds(List<String> scriptIdList) {
        return scriptManager.batchGetOnlineScriptVersionByScriptIds(scriptIdList);
    }

    @Override
    public ScriptDTO updateScriptDesc(String operator, String scriptId, String desc) {
        return scriptManager.updateScriptDesc(operator, PUBLIC_APP_ID, scriptId, desc);
    }

    @Override
    public ScriptDTO updateScriptName(String operator, String scriptId, String newName) {
        return scriptManager.updateScriptName(operator, PUBLIC_APP_ID, scriptId, newName);
    }

    @Override
    public ScriptDTO updateScriptTags(String operator,
                                      String scriptId,
                                      List<TagDTO> tags) {
        return scriptManager.updateScriptTags(operator, PUBLIC_APP_ID, scriptId, tags);
    }

    @Override
    public List<String> listScriptNames(String keyword) {
        return scriptManager.listScriptNames(PUBLIC_APP_ID, keyword);
    }

    @Override
    public List<ScriptDTO> listOnlineScript() {
        return scriptManager.listOnlineScriptForApp(PUBLIC_APP_ID);
    }

    @Override
    public ScriptDTO getOnlineScriptVersionByScriptId(String scriptId) {
        return scriptManager.getOnlineScriptVersionByScriptId(PUBLIC_APP_ID, scriptId);
    }

    @Override
    public PageData<ScriptDTO> listPageScriptVersion(ScriptQuery scriptQuery) {
        return scriptManager.listPageScriptVersion(scriptQuery);
    }

    @Override
    public List<SyncScriptResultDTO> syncScriptToTaskTemplate(String operator,
                                                              String scriptId,
                                                              Long syncScriptVersionId,
                                                              List<TemplateStepIDDTO> templateStepIDs) {
        return scriptManager.syncScriptToTaskTemplate(operator, PUBLIC_APP_ID, scriptId, syncScriptVersionId,
            templateStepIDs);
    }

    @Override
    public List<String> listScriptIds() {
        return scriptManager.listScriptIds(PUBLIC_APP_ID);
    }

    @Override
    public TagCountVO getTagScriptCount() {
        return scriptManager.getTagScriptCount(PUBLIC_APP_ID);
    }

    @Override
    public boolean isExistAnyPublicScript() {
        return scriptManager.isExistAnyScript(PUBLIC_APP_ID);
    }

    @Override
    public ScriptDTO getByScriptIdAndVersion(String scriptId, String version) {
        return scriptManager.getByScriptIdAndVersion(PUBLIC_APP_ID, scriptId, version);
    }

    @Override
    public ScriptDTO getScriptByScriptId(String scriptId) {
        return scriptManager.getScriptByScriptId(scriptId);
    }
}

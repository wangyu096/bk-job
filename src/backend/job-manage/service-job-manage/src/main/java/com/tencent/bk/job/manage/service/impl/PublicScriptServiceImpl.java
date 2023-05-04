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

import com.tencent.bk.audit.annotations.ActionAuditRecord;
import com.tencent.bk.audit.annotations.AuditAttribute;
import com.tencent.bk.audit.annotations.AuditInstanceRecord;
import com.tencent.bk.audit.model.ActionAuditContext;
import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.exception.NotFoundException;
import com.tencent.bk.job.common.iam.constant.ActionId;
import com.tencent.bk.job.common.iam.constant.ResourceTypeId;
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

import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_ID;
import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_NAME;
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
    @ActionAuditRecord(
        actionId = ActionId.CREATE_PUBLIC_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.PUBLIC_SCRIPT,
            instanceIds = "#script?.id",
            instanceNames = "#$?.name"
        ),
        content = "Create public script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO saveScript(ScriptDTO script) {
        return scriptManager.saveScript(script);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.CREATE_PUBLIC_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.PUBLIC_SCRIPT,
            instanceIds = "#scriptId"
        ),
        content = "Delete public script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public void deleteScript(String scriptId) {
        ScriptDTO script = getScript(scriptId);
        if (script == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }

        ActionAuditContext.current().setInstanceName(script.getName());

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
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_PUBLIC_SCRIPT_INSTANCE,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.PUBLIC_SCRIPT,
            instanceIds = "#scriptVersion?.id",
            instanceNames = "#$?.name"
        ),
        attributes = @AuditAttribute(
            name = "@VERSION", value = "#scriptVersion?.version"
        ),
        content =
            "Create a new version [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})({{@VERSION}})"
    )
    public ScriptDTO saveScriptVersion(ScriptDTO scriptVersion) {
        return scriptManager.saveScriptVersion(scriptVersion);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_PUBLIC_SCRIPT_INSTANCE,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.PUBLIC_SCRIPT
        ),
        content =
            "Modify script version [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})({{@VERSION}})"
    )
    public ScriptDTO updateScriptVersion(ScriptDTO scriptVersion) {
        ScriptDTO originScript = getScriptByScriptId(scriptVersion.getId());
        if (originScript == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }
        ScriptDTO updateScript = scriptManager.updateScriptVersion(scriptVersion);

        // 审计
        ActionAuditContext.current()
            .setInstanceId(originScript.getId())
            .setInstanceName(originScript.getName())
            .setOriginInstance(originScript.toEsbScriptV3DTO())
            .setInstance(updateScript.toEsbScriptV3DTO())
            .addAttribute("@VERSION", originScript.getVersion());

        return updateScript;
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_PUBLIC_SCRIPT_INSTANCE,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.PUBLIC_SCRIPT
        ),
        content = "Delete script version [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})({{@VERSION}})"
    )
    public void deleteScriptVersion(Long scriptVersionId) {
        addScriptVersionAuditInfo(scriptVersionId);
        scriptManager.deleteScriptVersion(PUBLIC_APP_ID, scriptVersionId);
    }

    private void addScriptVersionAuditInfo(Long scriptVersionId) {
        ScriptDTO script = getScriptVersion(scriptVersionId);
        if (script == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }
        ActionAuditContext.current()
            .setInstanceId(script.getId())
            .setInstanceName(script.getName())
            .addAttribute("@VERSION", script.getVersion());
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_PUBLIC_SCRIPT_INSTANCE,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.PUBLIC_SCRIPT
        ),
        content = "Set script version [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})({{@VERSION}}) state to " +
            "[online]"
    )
    public void publishScript(String scriptId, Long scriptVersionId) {
        addScriptVersionAuditInfo(scriptVersionId);
        scriptManager.publishScript(PUBLIC_APP_ID, scriptId, scriptVersionId);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_PUBLIC_SCRIPT_INSTANCE,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.PUBLIC_SCRIPT
        ),
        content = "Set script version [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})({{@VERSION}}) state to " +
            "[forbidden]"
    )
    public void disableScript(String scriptId, Long scriptVersionId) {
        addScriptVersionAuditInfo(scriptVersionId);
        scriptManager.disableScript(PUBLIC_APP_ID, scriptId, scriptVersionId);
    }

    @Override
    public Map<String, ScriptDTO> batchGetOnlineScriptVersionByScriptIds(List<String> scriptIdList) {
        return scriptManager.batchGetOnlineScriptVersionByScriptIds(scriptIdList);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_PUBLIC_SCRIPT_INSTANCE,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.PUBLIC_SCRIPT
        ),
        content = "Modify public script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO updateScriptDesc(String operator, String scriptId, String desc) {
        ScriptDTO originScript = getScript(scriptId);
        if (originScript == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }

        ScriptDTO updateScript = scriptManager.updateScriptDesc(operator, PUBLIC_APP_ID, scriptId, desc);

        // 审计
        ActionAuditContext.current()
            .setInstanceId(scriptId)
            .setInstanceName(originScript.getName())
            .setOriginInstance(originScript.toEsbScriptV3DTO())
            .setInstance(updateScript.toEsbScriptV3DTO());

        return updateScript;
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_PUBLIC_SCRIPT_INSTANCE,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.PUBLIC_SCRIPT
        ),
        content = "Modify public script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO updateScriptName(String operator, String scriptId, String newName) {
        ScriptDTO originScript = getScript(scriptId);
        if (originScript == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }

        ScriptDTO updateScript = scriptManager.updateScriptName(operator, PUBLIC_APP_ID, scriptId, newName);

        // 审计
        ActionAuditContext.current()
            .setInstanceId(scriptId)
            .setInstanceName(originScript.getName())
            .setOriginInstance(originScript.toEsbScriptV3DTO())
            .setInstance(updateScript.toEsbScriptV3DTO());

        return updateScript;
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_PUBLIC_SCRIPT_INSTANCE,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.PUBLIC_SCRIPT
        ),
        content = "Modify public script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO updateScriptTags(String operator,
                                      String scriptId,
                                      List<TagDTO> tags) {
        ScriptDTO originScript = getScript(scriptId);
        if (originScript == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }

        ScriptDTO updateScript = scriptManager.updateScriptTags(operator, PUBLIC_APP_ID, scriptId, tags);

        // 审计
        ActionAuditContext.current()
            .setInstanceId(scriptId)
            .setInstanceName(originScript.getName())
            .setOriginInstance(originScript.toEsbScriptV3DTO())
            .setInstance(updateScript.toEsbScriptV3DTO());

        return updateScript;
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

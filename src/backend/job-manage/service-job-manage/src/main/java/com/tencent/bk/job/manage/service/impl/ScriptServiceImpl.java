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
import com.tencent.bk.job.manage.service.ScriptManager;
import com.tencent.bk.job.manage.service.ScriptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_ID;
import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_NAME;

/**
 * 业务脚本Service
 */
@Slf4j
@Service
public class ScriptServiceImpl implements ScriptService {
    private final ScriptManager scriptManager;

    @Autowired
    public ScriptServiceImpl(ScriptManager scriptManager) {
        this.scriptManager = scriptManager;
    }

    @Override
    public PageData<ScriptDTO> listPageScript(ScriptQuery scriptQuery) {
        return scriptManager.listPageScript(scriptQuery);
    }

    @Override
    public List<ScriptDTO> listScripts(ScriptQuery scriptQuery) {
        return scriptManager.listScripts(scriptQuery);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.VIEW_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#$?.id",
            instanceNames = "#$?.name"
        ),
        content = "View script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO getScript(Long appId, String scriptId) {
        return scriptManager.getScript(appId, scriptId);
    }

    @Override
    public List<ScriptBasicDTO> listScriptBasicInfoByScriptIds(Collection<String> scriptIds) {
        return scriptManager.listScriptBasicInfoByScriptIds(scriptIds);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.CREATE_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#$?.id",
            instanceNames = "#$?.name"
        ),
        content = "Create script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO saveScript(ScriptDTO script) {
        return scriptManager.saveScript(script);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#scriptId"
        ),
        content = "Delete script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public void deleteScript(Long appId, String scriptId) {
        ScriptDTO script = getScript(appId, scriptId);
        if (script == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }

        ActionAuditContext.current().setInstanceName(script.getName());

        scriptManager.deleteScript(appId, scriptId);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.VIEW_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#$?.id",
            instanceNames = "#$?.name"
        ),
        content = "View script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO getScriptVersion(long appId, Long scriptVersionId) {
        return scriptManager.getScriptVersion(appId, scriptVersionId);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.VIEW_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#$?.id",
            instanceNames = "#$?.name"
        ),
        content = "View script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO getScriptVersion(Long scriptVersionId) {
        return scriptManager.getScriptVersion(scriptVersionId);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.VIEW_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#scriptId",
            instanceNames = "#$[0]?.name"
        ),
        content = "View script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public List<ScriptDTO> listScriptVersion(long appId, String scriptId) {
        return scriptManager.listScriptVersion(appId, scriptId);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#scriptVersion?.id",
            instanceNames = "#$?.name"
        ),
        attributes = @AuditAttribute(
            name = "@VERSION", value = "#scriptVersion?.version"
        ),
        content = "Create a new version ({{@VERSION}}) for script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO saveScriptVersion(ScriptDTO scriptVersion) {
        return scriptManager.saveScriptVersion(scriptVersion);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#scriptVersion?.id",
            instanceNames = "#$?.name"
        ),
        content = "Modify script version ({{@VERSION}}) for script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO updateScriptVersion(ScriptDTO scriptVersion) {
        // 审计
        ScriptDTO originScript = getScriptByScriptId(scriptVersion.getId());
        if (originScript == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }
        ActionAuditContext.current().setInstanceName(originScript.getName());
        ActionAuditContext.current().setOriginInstance(originScript.toEsbScriptV3DTO());
        ActionAuditContext.current().addAttribute("@VERSION", originScript.getVersion());

        ScriptDTO updateScript = scriptManager.updateScriptVersion(scriptVersion);

        ActionAuditContext.current().setInstance(updateScript.toEsbScriptV3DTO());

        return updateScript;
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT
        ),
        content = "Delete script version({{@VERSION}}) for script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public void deleteScriptVersion(String operator, Long appId, Long scriptVersionId) {
        addScriptVersionAuditInfo(appId, scriptVersionId);

        scriptManager.deleteScriptVersion(appId, scriptVersionId);
    }

    private void addScriptVersionAuditInfo(Long appId, Long scriptVersionId) {
        ScriptDTO script = getScriptVersion(appId, scriptVersionId);
        if (script == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }
        ActionAuditContext.current().setInstanceId(script.getId());
        ActionAuditContext.current().setInstanceName(script.getName());
        ActionAuditContext.current().addAttribute("{{@VERSION}}", script.getVersion());
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT
        ),
        content = "Publish script version({{@VERSION}}) for script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public void publishScript(Long appId, String operator, String scriptId, Long scriptVersionId) {
        addScriptVersionAuditInfo(appId, scriptVersionId);

        scriptManager.publishScript(appId, scriptId, scriptVersionId);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT
        ),
        content = "Disable script version({{@VERSION}}) for script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public void disableScript(Long appId, String operator, String scriptId, Long scriptVersionId) {
        addScriptVersionAuditInfo(appId, scriptVersionId);

        scriptManager.disableScript(appId, scriptId, scriptVersionId);
    }

    @Override
    public Map<String, ScriptDTO> batchGetOnlineScriptVersionByScriptIds(List<String> scriptIdList) {
        return scriptManager.batchGetOnlineScriptVersionByScriptIds(scriptIdList);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#scriptId",
            instanceNames = "#$?.name"
        ),
        content = "Modify script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO updateScriptDesc(Long appId, String operator, String scriptId, String desc) {
        ScriptDTO originScript = getScript(appId, scriptId);
        if (originScript == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }

        // 审计
        ActionAuditContext.current().setOriginInstance(originScript.toEsbScriptV3DTO());

        ScriptDTO updateScript = scriptManager.updateScriptDesc(operator, appId, scriptId, desc);

        ActionAuditContext.current().setInstance(updateScript.toEsbScriptV3DTO());

        return updateScript;
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#scriptId",
            instanceNames = "#$?.name"
        ),
        content = "Modify script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO updateScriptName(Long appId, String operator, String scriptId, String newName) {
        ScriptDTO originScript = getScript(appId, scriptId);
        if (originScript == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }

        // 审计
        ActionAuditContext.current().setOriginInstance(originScript.toEsbScriptV3DTO());

        ScriptDTO updateScript = scriptManager.updateScriptName(operator, appId, scriptId, newName);

        ActionAuditContext.current().setInstance(updateScript.toEsbScriptV3DTO());

        return updateScript;
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#scriptId",
            instanceNames = "#$?.name"
        ),
        content = "Modify script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO updateScriptTags(Long appId,
                                      String operator,
                                      String scriptId,
                                      List<TagDTO> tags) {
        ScriptDTO originScript = getScript(appId, scriptId);
        if (originScript == null) {
            throw new NotFoundException(ErrorCode.SCRIPT_NOT_EXIST);
        }

        // 审计
        ActionAuditContext.current().setOriginInstance(originScript.toEsbScriptV3DTO());

        ScriptDTO updateScript = scriptManager.updateScriptTags(operator, appId, scriptId, tags);

        ActionAuditContext.current().setInstance(updateScript.toEsbScriptV3DTO());

        return updateScript;
    }

    @Override
    public List<String> listScriptNames(Long appId, String keyword) {
        return scriptManager.listScriptNames(appId, keyword);
    }

    @Override
    public List<ScriptDTO> listOnlineScript(String operator, long appId) {
        return scriptManager.listOnlineScriptForApp(appId);
    }

    @Override
    public ScriptDTO getOnlineScriptVersionByScriptId(long appId, String scriptId) {
        return scriptManager.getOnlineScriptVersionByScriptId(appId, scriptId);
    }

    @Override
    public PageData<ScriptDTO> listPageScriptVersion(ScriptQuery scriptQuery) {
        return scriptManager.listPageScriptVersion(scriptQuery);
    }

    @Override
    public List<SyncScriptResultDTO> syncScriptToTaskTemplate(String username,
                                                              Long appId,
                                                              String scriptId,
                                                              Long syncScriptVersionId,
                                                              List<TemplateStepIDDTO> templateStepIDs) {
        return scriptManager.syncScriptToTaskTemplate(username, appId, scriptId, syncScriptVersionId, templateStepIDs);
    }

    @Override
    public boolean isExistAnyAppScript(long appId) {
        return scriptManager.isExistAnyScript(appId);
    }

    @Override
    public TagCountVO getTagScriptCount(Long appId) {
        return scriptManager.getTagScriptCount(appId);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.VIEW_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#scriptId",
            instanceNames = "#$?.name"
        ),
        content = "View script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO getByScriptIdAndVersion(Long appId, String scriptId, String version) {
        return scriptManager.getByScriptIdAndVersion(appId, scriptId, version);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.VIEW_SCRIPT,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.SCRIPT,
            instanceIds = "#scriptId",
            instanceNames = "#$?.name"
        ),
        content = "View script [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public ScriptDTO getScriptByScriptId(String scriptId) {
        return scriptManager.getScriptByScriptId(scriptId);
    }
}

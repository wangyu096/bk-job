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
import com.tencent.bk.audit.annotations.AuditInstanceRecord;
import com.tencent.bk.audit.context.ActionAuditContext;
import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.exception.NotFoundException;
import com.tencent.bk.job.common.iam.constant.ActionId;
import com.tencent.bk.job.common.iam.constant.ResourceTypeId;
import com.tencent.bk.job.common.model.BaseSearchCondition;
import com.tencent.bk.job.common.model.PageData;
import com.tencent.bk.job.manage.dao.CredentialDAO;
import com.tencent.bk.job.manage.model.dto.CredentialDTO;
import com.tencent.bk.job.manage.model.inner.resp.ServiceCredentialDisplayDTO;
import com.tencent.bk.job.manage.model.web.request.CredentialCreateUpdateReq;
import com.tencent.bk.job.manage.service.CredentialService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_ID;
import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_NAME;

@Service
public class CredentialServiceImpl implements CredentialService {

    private final DSLContext dslContext;
    private final CredentialDAO credentialDAO;

    @Autowired
    public CredentialServiceImpl(DSLContext dslContext, CredentialDAO credentialDAO) {
        this.dslContext = dslContext;
        this.credentialDAO = credentialDAO;
    }

    @Override
    public PageData<CredentialDTO> listCredentials(
        CredentialDTO credentialQuery,
        BaseSearchCondition baseSearchCondition
    ) {
        return credentialDAO.listCredentials(credentialQuery, baseSearchCondition);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.CREATE_TICKET,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.TICKET,
            instanceIds = "#$?.id",
            instanceNames = "#$?.name"
        ),
        content = "Create credential [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public CredentialDTO createCredential(String username, Long appId, CredentialCreateUpdateReq createUpdateReq) {
        CredentialDTO credentialDTO = buildCredentialDTO(username, appId, createUpdateReq);
        credentialDTO.setCreator(username);
        credentialDTO.setCreateTime(credentialDTO.getLastModifyTime());
        String id = credentialDAO.insertCredential(dslContext, credentialDTO);

        return getCredentialById(id);
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_TICKET,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.TICKET
        ),
        content = "Modify credential [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public CredentialDTO updateCredential(String username, Long appId, CredentialCreateUpdateReq createUpdateReq) {
        String id = createUpdateReq.getId();
        CredentialDTO credentialDTO = buildCredentialDTO(username, appId, createUpdateReq);
        CredentialDTO originCredentialDTO = credentialDAO.getCredentialById(dslContext, id);
        if (originCredentialDTO == null) {
            throw new NotFoundException(ErrorCode.CREDENTIAL_NOT_EXIST);
        }

        String value1 = createUpdateReq.getValue1();
        if ("******".equals(value1)) {
            credentialDTO.setFirstValue(originCredentialDTO.getFirstValue());
        } else {
            credentialDTO.setFirstValue(value1);
        }
        String value2 = createUpdateReq.getValue2();
        if ("******".equals(value2)) {
            credentialDTO.setSecondValue(originCredentialDTO.getSecondValue());
        } else {
            credentialDTO.setSecondValue(value2);
        }
        credentialDAO.updateCredentialById(dslContext, credentialDTO);

        CredentialDTO updateCredential = getCredentialById(id);

        // 审计
        ActionAuditContext.current()
            .setInstanceId(id)
            .setInstanceName(originCredentialDTO.getName())
            .setOriginInstance(originCredentialDTO.toEsbCredentialSimpleInfoV3DTO())
            .setInstance(updateCredential.toEsbCredentialSimpleInfoV3DTO());

        return updateCredential;
    }

    @Override
    @ActionAuditRecord(
        actionId = ActionId.MANAGE_TICKET,
        instance = @AuditInstanceRecord(
            resourceType = ResourceTypeId.TICKET,
            instanceIds = "#id"
        ),
        content = "Delete credential [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})"
    )
    public Integer deleteCredentialById(String username, Long appId, String id) {
        CredentialDTO credential = getCredentialById(id);
        if (credential == null) {
            throw new NotFoundException(ErrorCode.CREDENTIAL_NOT_EXIST);
        }

        // 审计
        ActionAuditContext.current().setInstanceName(credential.getName());

        return credentialDAO.deleteCredentialById(dslContext, id);
    }

    @Override
    public CredentialDTO getCredentialById(Long appId, String id) {
        CredentialDTO credentialDTO = getCredentialById(id);
        if (credentialDTO == null || !credentialDTO.getAppId().equals(appId)) {
            return null;
        } else {
            return credentialDTO;
        }
    }

    @Override
    public CredentialDTO getCredentialById(String id) {
        return credentialDAO.getCredentialById(dslContext, id);
    }

    @Override
    public List<ServiceCredentialDisplayDTO> listCredentialDisplayInfoByIds(Collection<String> ids) {
        return credentialDAO.listCredentialDisplayInfoByIds(dslContext, ids);
    }

    private CredentialDTO buildCredentialDTO(String username, Long appId, CredentialCreateUpdateReq createUpdateReq) {
        CredentialDTO credentialDTO = new CredentialDTO();
        credentialDTO.setId(createUpdateReq.getId());
        credentialDTO.setAppId(appId);
        credentialDTO.setName(createUpdateReq.getName());
        credentialDTO.setType(createUpdateReq.getType().name());
        credentialDTO.setDescription(createUpdateReq.getDescription());
        credentialDTO.setCredential(createUpdateReq.toCommonCredential());
        credentialDTO.setLastModifyUser(username);
        credentialDTO.setLastModifyTime(System.currentTimeMillis());
        return credentialDTO;
    }
}

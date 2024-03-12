package com.tencent.bk.job.file_gateway.api.esb;

import com.tencent.bk.audit.annotations.AuditEntry;
import com.tencent.bk.audit.annotations.AuditRequestBody;
import com.tencent.bk.job.common.constant.ErrorCode;
import com.tencent.bk.job.common.error.SubErrorCode;
import com.tencent.bk.job.common.error.payload.ResourceInfoPayloadDTO;
import com.tencent.bk.job.common.exception.base.AlreadyExistsException;
import com.tencent.bk.job.common.exception.base.InvalidParamException;
import com.tencent.bk.job.common.exception.base.NotFoundException;
import com.tencent.bk.job.common.iam.constant.ActionId;
import com.tencent.bk.job.common.iam.constant.ResourceTypeEnum;
import com.tencent.bk.job.common.openapi.job.v3.EsbResp;
import com.tencent.bk.job.common.util.MessageFormatUtil;
import com.tencent.bk.job.file_gateway.consts.WorkerSelectModeEnum;
import com.tencent.bk.job.file_gateway.consts.WorkerSelectScopeEnum;
import com.tencent.bk.job.file_gateway.model.dto.FileSourceDTO;
import com.tencent.bk.job.file_gateway.model.dto.FileSourceTypeDTO;
import com.tencent.bk.job.file_gateway.model.req.esb.v3.EsbCreateOrUpdateFileSourceV3Req;
import com.tencent.bk.job.file_gateway.model.resp.esb.v3.EsbFileSourceSimpleInfoV3DTO;
import com.tencent.bk.job.file_gateway.service.FileSourceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@Slf4j
public class EsbFileSourceV3ResourceImpl implements EsbFileSourceV3Resource {

    private final FileSourceService fileSourceService;

    @Autowired
    public EsbFileSourceV3ResourceImpl(FileSourceService fileSourceService) {
        this.fileSourceService = fileSourceService;
    }

    @Override
    @AuditEntry(actionId = ActionId.CREATE_FILE_SOURCE)
    public EsbResp<EsbFileSourceSimpleInfoV3DTO> createFileSource(
        String username,
        String appCode,
        @AuditRequestBody EsbCreateOrUpdateFileSourceV3Req req) {
        Long appId = req.getAppId();
        checkCreateParam(req);
        FileSourceDTO fileSourceDTO = buildFileSourceDTO(username, appId, null, req);
        FileSourceDTO createdFileSource = fileSourceService.saveFileSource(username, appId, fileSourceDTO);
        return EsbResp.buildSuccessResp(new EsbFileSourceSimpleInfoV3DTO(createdFileSource.getId()));
    }

    @Override
    @AuditEntry(actionId = ActionId.MANAGE_FILE_SOURCE)
    public EsbResp<EsbFileSourceSimpleInfoV3DTO> updateFileSource(
        String username,
        String appCode,
        @AuditRequestBody EsbCreateOrUpdateFileSourceV3Req req) {
        Integer id = checkUpdateParamAndGetId(req);
        Long appId = req.getAppId();
        FileSourceDTO fileSourceDTO = buildFileSourceDTO(username, appId, id, req);
        FileSourceDTO updateFileSource = fileSourceService.updateFileSourceById(
            username, appId, fileSourceDTO);
        return EsbResp.buildSuccessResp(new EsbFileSourceSimpleInfoV3DTO(updateFileSource.getId()));
    }

    private void checkCommonParam(EsbCreateOrUpdateFileSourceV3Req req) {
        if (StringUtils.isBlank(req.getAlias())) {
            throw InvalidParamException.withInvalidField("alias", "File source alias is empty");
        }
        if (StringUtils.isBlank(req.getType())) {
            throw InvalidParamException.withInvalidField("type", "File source type is empty");
        }
        if (StringUtils.isBlank(req.getCredentialId())) {
            throw InvalidParamException.withInvalidField("credential_id",
                "File source credential_id is empty");
        }
    }

    private void checkCreateParam(EsbCreateOrUpdateFileSourceV3Req req) {
        String code = req.getCode();
        if (StringUtils.isBlank(code)) {
            throw InvalidParamException.withInvalidField("code", "File source code is empty");
        }
        FileSourceTypeDTO fileSourceTypeDTO = fileSourceService.getFileSourceTypeByCode(
            req.getType()
        );
        if (fileSourceTypeDTO == null) {
            throw InvalidParamException.withInvalidField("type", "File source type is empty");
        }
        if (fileSourceService.existsCode(req.getAppId(), code)) {
            throw new AlreadyExistsException(SubErrorCode.of(ErrorCode.FILE_SOURCE_CODE_ALREADY_EXISTS, code));
        }
        checkCommonParam(req);
    }

    private Integer checkUpdateParamAndGetId(EsbCreateOrUpdateFileSourceV3Req req) {
        Long appId = req.getAppId();
        String code = req.getCode();
        if (StringUtils.isBlank(code)) {
            throw InvalidParamException.withInvalidField("code", "File source code is empty");
        }
        Integer id = fileSourceService.getFileSourceIdByCode(appId, code);
        if (id == null) {
            throw new NotFoundException(SubErrorCode.of(ErrorCode.FILE_SOURCE_NOT_EXIST),
                new ResourceInfoPayloadDTO(
                    ResourceTypeEnum.FILE_SOURCE.getId(),
                    "FileSourceCode:" + code,
                    MessageFormatUtil.format("File source with code : {} not found")));
        }
        if (!fileSourceService.existsFileSource(appId, id)) {
            throw new NotFoundException(SubErrorCode.of(ErrorCode.FILE_SOURCE_ID_NOT_IN_BIZ),
                new ResourceInfoPayloadDTO(
                    ResourceTypeEnum.FILE_SOURCE.getId(),
                    "FileSourceCode:" + code,
                    MessageFormatUtil.format("File source with code : {} not found")));
        }
        if (StringUtils.isNotBlank(req.getType())) {
            FileSourceTypeDTO fileSourceTypeDTO = fileSourceService.getFileSourceTypeByCode(
                req.getType()
            );
            if (fileSourceTypeDTO == null) {
                throw InvalidParamException.withInvalidField("type");
            }
        }
        return id;
    }

    private FileSourceDTO buildFileSourceDTO(String username,
                                             Long appId,
                                             Integer id,
                                             EsbCreateOrUpdateFileSourceV3Req fileSourceCreateUpdateReq) {
        FileSourceDTO fileSourceDTO = new FileSourceDTO();
        fileSourceDTO.setAppId(appId);
        fileSourceDTO.setId(id);
        fileSourceDTO.setCode(fileSourceCreateUpdateReq.getCode());
        fileSourceDTO.setAlias(fileSourceCreateUpdateReq.getAlias());
        fileSourceDTO.setStatus(null);
        fileSourceDTO.setFileSourceType(
            fileSourceService.getFileSourceTypeByCode(
                fileSourceCreateUpdateReq.getType()
            )
        );
        fileSourceDTO.setFileSourceInfoMap(fileSourceCreateUpdateReq.getAccessParams());
        fileSourceDTO.setPublicFlag(false);
        fileSourceDTO.setSharedAppIdList(Collections.emptyList());
        fileSourceDTO.setShareToAllApp(false);
        fileSourceDTO.setCredentialId(fileSourceCreateUpdateReq.getCredentialId());
        fileSourceDTO.setFilePrefix(fileSourceCreateUpdateReq.getFilePrefix());
        fileSourceDTO.setWorkerSelectScope(WorkerSelectScopeEnum.PUBLIC.name());
        fileSourceDTO.setWorkerSelectMode(WorkerSelectModeEnum.AUTO.name());
        fileSourceDTO.setWorkerId(null);
        // 文件源默认开启状态
        fileSourceDTO.setEnable(true);
        fileSourceDTO.setCreator(username);
        fileSourceDTO.setCreateTime(System.currentTimeMillis());
        fileSourceDTO.setLastModifyUser(username);
        fileSourceDTO.setLastModifyTime(System.currentTimeMillis());
        return fileSourceDTO;
    }
}

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

package com.tencent.bk.job.manage.api.web.impl;

import com.tencent.bk.job.common.cc.model.InstanceTopologyDTO;
import com.tencent.bk.job.common.constant.AppTypeEnum;
import com.tencent.bk.job.common.iam.dto.AppIdResult;
import com.tencent.bk.job.common.iam.service.AuthService;
import com.tencent.bk.job.common.model.BaseSearchCondition;
import com.tencent.bk.job.common.model.PageData;
import com.tencent.bk.job.common.model.WebResponse;
import com.tencent.bk.job.common.model.dto.ApplicationHostInfoDTO;
import com.tencent.bk.job.common.model.dto.ApplicationInfoDTO;
import com.tencent.bk.job.common.model.dto.DynamicGroupInfoDTO;
import com.tencent.bk.job.common.model.vo.HostInfoVO;
import com.tencent.bk.job.common.model.vo.TargetNodeVO;
import com.tencent.bk.job.common.util.CompareUtil;
import com.tencent.bk.job.common.util.JobContextUtil;
import com.tencent.bk.job.common.util.PageUtil;
import com.tencent.bk.job.manage.api.web.WebAppResource;
import com.tencent.bk.job.manage.common.TopologyHelper;
import com.tencent.bk.job.manage.model.dto.ApplicationFavorDTO;
import com.tencent.bk.job.manage.model.web.request.AgentStatisticsReq;
import com.tencent.bk.job.manage.model.web.request.FavorAppReq;
import com.tencent.bk.job.manage.model.web.request.IpCheckReq;
import com.tencent.bk.job.manage.model.web.request.ipchooser.AppTopologyTreeNode;
import com.tencent.bk.job.manage.model.web.request.ipchooser.ListHostByBizTopologyNodesReq;
import com.tencent.bk.job.manage.model.web.vo.AppVO;
import com.tencent.bk.job.manage.model.web.vo.CcTopologyNodeVO;
import com.tencent.bk.job.manage.model.web.vo.DynamicGroupInfoVO;
import com.tencent.bk.job.manage.model.web.vo.NodeInfoVO;
import com.tencent.bk.job.manage.model.web.vo.PageDataWithAvailableIdList;
import com.tencent.bk.job.manage.model.web.vo.index.AgentStatistics;
import com.tencent.bk.job.manage.service.ApplicationService;
import com.tencent.bk.job.manage.service.impl.ApplicationFavorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class WebAppResourceImpl implements WebAppResource {

    private final ApplicationService applicationService;
    private final ApplicationFavorService applicationFavorService;
    private final AuthService authService;

    @Autowired
    public WebAppResourceImpl(
        ApplicationService applicationService,
        ApplicationFavorService applicationFavorService,
        AuthService authService
    ) {
        this.applicationService = applicationService;
        this.applicationFavorService = applicationFavorService;
        this.authService = authService;
    }

    // 老接口，在listAppWithFavor上线后下掉
    @Deprecated
    @Override
    public WebResponse<List<AppVO>> listApp(String username) {
        List<ApplicationInfoDTO> appList = applicationService.listAllAppsFromLocalDB();
        List<ApplicationInfoDTO> normalAppList =
            appList.parallelStream().filter(it -> it.getAppType() == AppTypeEnum.NORMAL).collect(Collectors.toList());
        appList.removeAll(normalAppList);
        // 业务集/全业务根据运维角色鉴权
        List<ApplicationInfoDTO> specialAppList =
            appList.parallelStream().filter(it -> it.getMaintainers().contains(username)).collect(Collectors.toList());
        AppIdResult appIdResult = authService.getAppIdList(username,
            normalAppList.parallelStream().map(ApplicationInfoDTO::getId).collect(Collectors.toList()));
        List<ApplicationInfoDTO> finalAppList = new ArrayList<>();
        if (appIdResult.getAny()) {
            finalAppList.addAll(normalAppList);
        } else {
            // 普通业务根据权限中心结果鉴权
            normalAppList =
                normalAppList.parallelStream().filter(it ->
                    appIdResult.getAppId().contains(it.getId())).collect(Collectors.toList());
            finalAppList.addAll(normalAppList);
        }
        finalAppList.addAll(specialAppList);
        List<AppVO> appVOList = finalAppList.parallelStream().map(it -> new AppVO(it.getId(), it.getName(),
            it.getAppType().getValue(), true, null, null)).collect(Collectors.toList());
        return WebResponse.buildSuccessResp(appVOList);
    }

    @Override
    public WebResponse<PageDataWithAvailableIdList<AppVO, Long>> listAppWithFavor(
        String username,
        Integer start,
        Integer pageSize
    ) {
        List<ApplicationInfoDTO> appList = applicationService.listAllAppsFromLocalDB();
        List<ApplicationInfoDTO> normalAppList =
            appList.parallelStream().filter(it -> it.getAppType() == AppTypeEnum.NORMAL).collect(Collectors.toList());
        appList.removeAll(normalAppList);
        // 业务集/全业务根据运维角色鉴权
        List<ApplicationInfoDTO> specialAppList =
            appList.parallelStream().filter(it -> it.getMaintainers().contains(username)).collect(Collectors.toList());
        AppIdResult appIdResult = authService.getAppIdList(username,
            normalAppList.parallelStream().map(ApplicationInfoDTO::getId).collect(Collectors.toList()));
        List<AppVO> finalAppList = new ArrayList<>();
        // 可用的普通业务Id
        List<Long> authorizedAppIds = appIdResult.getAppId();
        // 所有可用的AppId(含业务集、全业务Id)
        List<Long> availableAppIds = new ArrayList<>(authorizedAppIds);
        if (appIdResult.getAny()) {
            for (ApplicationInfoDTO normalApp : normalAppList) {
                AppVO appVO = new AppVO(normalApp.getId(), normalApp.getName(), normalApp.getAppType().getValue(),
                    true, null, null);
                finalAppList.add(appVO);
                availableAppIds.add(normalApp.getId());
            }
        } else {
            // 普通业务根据权限中心结果鉴权
            for (ApplicationInfoDTO normalApp : normalAppList) {
                AppVO appVO = new AppVO(normalApp.getId(), normalApp.getName(), normalApp.getAppType().getValue(),
                    null, null, null);
                if (authorizedAppIds.contains(normalApp.getId())) {
                    appVO.setHasPermission(true);
                    finalAppList.add(appVO);
                } else {
                    appVO.setHasPermission(false);
                    finalAppList.add(appVO);
                }
            }
        }
        for (ApplicationInfoDTO specialApp : specialAppList) {
            AppVO appVO = new AppVO(specialApp.getId(), specialApp.getName(), specialApp.getAppType().getValue(),
                true, null, null);
            finalAppList.add(appVO);
            availableAppIds.add(specialApp.getId());
        }
        // 收藏标识刷新
        List<ApplicationFavorDTO> applicationFavorDTOList = applicationFavorService.getAppFavorListByUsername(username);
        Map<Long, Long> appIdFavorTimeMap = new HashMap<>();
        for (ApplicationFavorDTO applicationFavorDTO : applicationFavorDTOList) {
            appIdFavorTimeMap.put(applicationFavorDTO.getAppId(), applicationFavorDTO.getFavorTime());
        }
        for (AppVO appVO : finalAppList) {
            if (appIdFavorTimeMap.containsKey(appVO.getId())) {
                appVO.setFavor(true);
                appVO.setFavorTime(appIdFavorTimeMap.get(appVO.getId()));
            } else {
                appVO.setFavor(false);
                appVO.setFavorTime(null);
            }
        }
        // 排序：有无权限、是否收藏、收藏时间倒序
        finalAppList.sort((o1, o2) -> {
            int result = o2.getHasPermission().compareTo(o1.getHasPermission());
            if (result != 0) {
                return result;
            } else {
                result = CompareUtil.safeCompareNullFront(o2.getFavor(), o1.getFavor());
            }
            if (result != 0) {
                return result;
            } else {
                return CompareUtil.safeCompareNullFront(o2.getFavorTime(), o1.getFavorTime());
            }
        });
        // 分页
        PageData<AppVO> pageData = PageUtil.pageInMem(finalAppList, start, pageSize);
        PageDataWithAvailableIdList<AppVO, Long> pageDataWithAvailableIdList =
            new PageDataWithAvailableIdList<>(pageData, availableAppIds);
        return WebResponse.buildSuccessResp(pageDataWithAvailableIdList);
    }

    @Override
    public WebResponse<Integer> favorApp(String username, Long appId, FavorAppReq req) {
        return WebResponse.buildSuccessResp(applicationFavorService.favorApp(username, req.getAppId()));
    }

    @Override
    public WebResponse<Integer> cancelFavorApp(String username, Long appId, FavorAppReq req) {
        return WebResponse.buildSuccessResp(applicationFavorService.cancelFavorApp(username, req.getAppId()));
    }

    @Override
    public WebResponse<PageData<HostInfoVO>> listAppHost(String username, Long appId, Integer start,
                                                         Integer pageSize, Long moduleType, String ipCondition) {
        JobContextUtil.setAppId(appId);

        ApplicationHostInfoDTO applicationHostInfoCondition = new ApplicationHostInfoDTO();
        applicationHostInfoCondition.setAppId(appId);
        applicationHostInfoCondition.setIp(ipCondition);
        if (moduleType != null) {
            applicationHostInfoCondition.getModuleType().add(moduleType);
        }

        BaseSearchCondition baseSearchCondition = new BaseSearchCondition();
        if (start == null || start < 0) {
            start = 0;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        baseSearchCondition.setStart(start);
        baseSearchCondition.setLength(pageSize);

        PageData<ApplicationHostInfoDTO> appHostInfoPageData =
            applicationService.listAppHost(applicationHostInfoCondition, baseSearchCondition);
        PageData<HostInfoVO> finalHostInfoPageData = new PageData<>();
        finalHostInfoPageData.setTotal(appHostInfoPageData.getTotal());
        finalHostInfoPageData.setStart(appHostInfoPageData.getStart());
        finalHostInfoPageData.setPageSize(appHostInfoPageData.getPageSize());
        finalHostInfoPageData
            .setData(appHostInfoPageData.getData().stream()
                .map(TopologyHelper::convertToHostInfoVO).collect(Collectors.toList()));
        return WebResponse.buildSuccessResp(finalHostInfoPageData);
    }

    @Override
    public WebResponse<CcTopologyNodeVO> listAppTopologyTree(String username, Long appId) {
        JobContextUtil.setAppId(appId);
        return WebResponse.buildSuccessResp(applicationService.listAppTopologyTree(username, appId));
    }

    @Override
    public WebResponse<CcTopologyNodeVO> listAppTopologyHostTree(String username, Long appId) {
        JobContextUtil.setAppId(appId);
        return WebResponse.buildSuccessResp(applicationService.listAppTopologyHostTree(username, appId));
    }

    @Override
    public WebResponse<CcTopologyNodeVO> listAppTopologyHostCountTree(String username, Long appId) {
        JobContextUtil.setAppId(appId);
        return WebResponse.buildSuccessResp(applicationService.listAppTopologyHostCountTree(username, appId));
    }

    @Override
    public WebResponse<PageData<HostInfoVO>> listHostByBizTopologyNodes(String username, Long appId,
                                                                        ListHostByBizTopologyNodesReq req) {
        return WebResponse.buildSuccessResp(applicationService.listHostByBizTopologyNodes(username, appId, req));
    }

    @Override
    public WebResponse<PageData<String>> listIpByBizTopologyNodes(String username, Long appId,
                                                                  ListHostByBizTopologyNodesReq req) {
        return WebResponse.buildSuccessResp(applicationService.listIPByBizTopologyNodes(username, appId, req));
    }

    @Override
    public WebResponse<List<AppTopologyTreeNode>> getNodeDetail(String username, Long appId,
                                                                List<TargetNodeVO> targetNodeVOList) {
        JobContextUtil.setAppId(appId);
        List<AppTopologyTreeNode> treeNodeList = applicationService.getAppTopologyTreeNodeDetail(username, appId,
            targetNodeVOList.stream().map(it -> new AppTopologyTreeNode(
                it.getType(),
                "",
                it.getId(),
                "",
                null
            )).collect(Collectors.toList()));
        return WebResponse.buildSuccessResp(treeNodeList);
    }

    @Override
    public WebResponse<List<List<CcTopologyNodeVO>>> queryNodePaths(String username, Long appId,
                                                                    List<TargetNodeVO> targetNodeVOList) {
        List<List<InstanceTopologyDTO>> pathList = applicationService.queryNodePaths(username, appId,
            targetNodeVOList.stream().map(it -> {
                InstanceTopologyDTO instanceTopologyDTO = new InstanceTopologyDTO();
                instanceTopologyDTO.setObjectId(it.getType());
                instanceTopologyDTO.setInstanceId(it.getId());
                return instanceTopologyDTO;
            }).collect(Collectors.toList()));
        List<List<CcTopologyNodeVO>> resultList = new ArrayList<>();
        for (List<InstanceTopologyDTO> instanceTopologyDTOS : pathList) {
            if (instanceTopologyDTOS == null) {
                resultList.add(null);
            } else {
                resultList.add(instanceTopologyDTOS.stream().map(it -> {
                    CcTopologyNodeVO ccTopologyNodeVO = new CcTopologyNodeVO();
                    ccTopologyNodeVO.setObjectId(it.getObjectId());
                    ccTopologyNodeVO.setObjectName(it.getObjectName());
                    ccTopologyNodeVO.setInstanceId(it.getInstanceId());
                    ccTopologyNodeVO.setInstanceName(it.getInstanceName());
                    return ccTopologyNodeVO;
                }).collect(Collectors.toList()));
            }
        }
        return WebResponse.buildSuccessResp(resultList);
    }

    @Override
    public WebResponse<List<NodeInfoVO>> listHostByNode(String username, Long appId,
                                                        List<TargetNodeVO> targetNodeVOList) {
        JobContextUtil.setAppId(appId);
        List<NodeInfoVO> moduleHostInfoList = applicationService.getHostsByNode(username, appId,
            targetNodeVOList.stream().map(it -> new AppTopologyTreeNode(
                it.getType(),
                "",
                it.getId(),
                "",
                null
            )).collect(Collectors.toList()));
        return WebResponse.buildSuccessResp(moduleHostInfoList);
    }

    @Override
    public WebResponse<List<DynamicGroupInfoVO>> listAppDynamicGroup(String username, Long appId) {
        JobContextUtil.setAppId(appId);
        ApplicationInfoDTO applicationInfoDTO = applicationService.getAppInfoById(appId);
        // 业务集动态分组暂不支持
        if (applicationInfoDTO.getAppType() != AppTypeEnum.NORMAL) {
            return WebResponse.buildSuccessResp(new ArrayList<>());
        }
        List<DynamicGroupInfoDTO> dynamicGroupList = applicationService.getDynamicGroupList(username, appId);
        List<DynamicGroupInfoVO> dynamicGroupInfoList = dynamicGroupList.parallelStream()
            .map(TopologyHelper::convertToDynamicGroupInfoVO)
            .collect(Collectors.toList());
        return WebResponse.buildSuccessResp(dynamicGroupInfoList);
    }

    @Override
    public WebResponse<List<DynamicGroupInfoVO>> listAppDynamicGroupHost(String username, Long appId,
                                                                         List<String> dynamicGroupIds) {
        JobContextUtil.setAppId(appId);
        List<DynamicGroupInfoDTO> dynamicGroupList =
            applicationService.getDynamicGroupHostList(username, appId, dynamicGroupIds);
        List<DynamicGroupInfoVO> dynamicGroupInfoList = dynamicGroupList.parallelStream()
            .map(TopologyHelper::convertToDynamicGroupInfoVO)
            .collect(Collectors.toList());
        return WebResponse.buildSuccessResp(dynamicGroupInfoList);
    }

    @Override
    public WebResponse<List<DynamicGroupInfoVO>> listAppDynamicGroupWithoutHosts(String username, Long appId,
                                                                                 List<String> dynamicGroupIds) {
        JobContextUtil.setAppId(appId);
        List<DynamicGroupInfoDTO> dynamicGroupList = applicationService.getDynamicGroupList(username, appId);
        List<DynamicGroupInfoVO> dynamicGroupInfoList = dynamicGroupList.parallelStream()
            .filter(dynamicGroupInfoDTO -> dynamicGroupIds.contains(dynamicGroupInfoDTO.getId()))
            .map(TopologyHelper::convertToDynamicGroupInfoVO)
            .collect(Collectors.toList());
        return WebResponse.buildSuccessResp(dynamicGroupInfoList);
    }

    @Override
    public WebResponse<List<HostInfoVO>> listHostByIp(String username, Long appId, IpCheckReq req) {
        return WebResponse.buildSuccessResp(applicationService.getHostsByIp(
            username,
            appId,
            req.getActionScope(),
            req.getIpList())
        );
    }

    @Override
    public WebResponse<AgentStatistics> agentStatistics(String username, Long appId,
                                                        AgentStatisticsReq agentStatisticsReq) {
        return WebResponse.buildSuccessResp(applicationService.getAgentStatistics(username, appId,
            agentStatisticsReq));
    }
}

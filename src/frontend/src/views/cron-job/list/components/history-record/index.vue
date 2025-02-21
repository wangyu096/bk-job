<!--
 * Tencent is pleased to support the open source community by making BK-JOB蓝鲸智云作业平台 available.
 *
 * Copyright (C) 2021 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-JOB蓝鲸智云作业平台 is licensed under the MIT License.
 *
 * License for BK-JOB蓝鲸智云作业平台:
 *
 *
 * Terms of the MIT License:
 * ---------------------------------------------------
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
-->

<template>
    <div class="cron-job-history-record">
        <div class="record-tab-wraper">
            <div class="tab-item" :class="{ active: listTab === 'launch' }" @click="handleTabChange('launch')">
                <div class="tab-name">{{ $t('cron.任务正常启动') }}</div>
                <Icon v-if="isLaunchLoading" type="sync-pending" svg class="loading-flag" />
                <div v-else class="tab-nums">{{ launchNums }}</div>
            </div>
            <div class="tab-item" :class="{ active: listTab === 'unlaunch' }" @click="handleTabChange('unlaunch')">
                <div class="tab-name">{{ $t('cron.任务未能启动') }}</div>
                <Icon v-if="isUnlaunchLoading" type="sync-pending" svg class="loading-flag" />
                <div v-else class="tab-nums">{{ unLaunchNums }}</div>
            </div>
        </div>
        <component :is="listCom" :data="data" v-bind="$attrs" v-on="$listeners" />
    </div>
</template>
<script>
    import TaskExecuteService from '@service/task-execute';
    import TimeTaskService from '@service/time-task';
    import LaunchList from './launch-list';
    import UnlaunchList from './unlaunch-list';

    const listComMap = {
        launch: LaunchList,
        unlaunch: UnlaunchList,
    };

    export default {
        name: '',
        props: {
            data: {
                type: Object,
                required: true,
            },
        },
        data () {
            return {
                listTab: 'launch',
                isLaunchLoading: true,
                isUnlaunchLoading: true,
                launchNums: 0,
                unLaunchNums: 0,
            };
        },
        computed: {
            listCom () {
                return listComMap[this.listTab];
            },
        },
        created () {
            this.fetchData();
        },
        methods: {
            handleTabChange (value) {
                this.listTab = value;
            },
            fetchData () {
                TaskExecuteService.fetchExecutionHistoryList({
                    cronTaskId: this.data.id,
                    startTime: '',
                    endTime: '',
                    start: 0,
                    pageSize: 1,
                    timeRange: 30,
                }).then((data) => {
                    this.launchNums = data.total;
                })
                    .finally(() => {
                        this.isLaunchLoading = false;
                    });
                TimeTaskService.fetchUnlaunchHistory({
                    cronTaskId: this.data.id,
                    start: 0,
                    pageSize: 1,
                    timeRange: 30,
                }).then((data) => {
                    this.unLaunchNums = data.total;
                })
                    .finally(() => {
                        this.isUnlaunchLoading = false;
                    });
            },
        },
    };
</script>
<style lang='postcss' scoped>
    .cron-job-history-record {
        .record-tab-wraper {
            display: flex;
            margin-bottom: 20px;

            .tab-item {
                display: flex;
                height: 32px;
                padding: 0 12px;
                margin-right: 20px;
                font-size: 14px;
                line-height: 32px;
                color: #63656e;
                cursor: pointer;
                border: 1px solid #c4c6cc;
                border-radius: 16px;
                transition: all 0.15s;
                align-items: center;

                &.active {
                    color: #fff;
                    background: #3a84ff;
                    border-color: #3a84ff;

                    .tab-nums {
                        color: #3a84ff;
                        background: #fff;
                    }

                    .loading-flag {
                        color: #fff;
                    }
                }
            }

            .tab-name {
                margin-right: 4px;
            }

            .tab-nums {
                height: 16px;
                padding: 0 4px;
                font-size: 12px;
                line-height: 16px;
                color: #979ba5;
                background: #f0f1f5;
                border-radius: 8px;
                transition: all 0.15s;
            }

            .loading-flag {
                color: #3a84ff;
                animation: sync-fetch-loading 1s linear infinite;
            }
        }
    }
</style>

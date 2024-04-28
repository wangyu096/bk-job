/*
 * Tencent is pleased to support the open source community by making BK-JOB蓝鲸智云作业平台 available.
 *
 * Copyright (C) 2021 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * BK-JOB蓝鲸智云作业平台 is licensed under the MIT License.
 *
 * License for BK-JOB蓝鲸智云作业平台:
 *
 * ---------------------------------------------------
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

/* eslint-disable max-len */
export default {
  namespace: 'history',
  message: {
    '搜索条件带任务ID时，将自动忽略其他条件': 'When condition is ID, other conditions will be ignored',
    '搜索任务ID，任务名称，执行方式，任务类型，任务状态，执行人...': 'Search by ID / Name / Generated by / Type / Launched by...',
    选择日期: 'Select datetime',
    '任务 ID': 'ID',
    耗时时长: 'Duration',
    操作: 'Actions',
    查看详情: 'Details',
    去重做: 'Relaunch',
    任务ID: 'ID',
    页面执行: 'Web UI',
    定时执行: 'Cron',
    API调用: 'API',
    作业执行: 'Job execution',
    脚本执行: 'Script execution',
    文件分发: 'File transfer',
    等待执行: 'Pending',
    正在执行: 'Running',
    执行成功: 'Successful',
    执行失败: 'Failed',
    等待确认: 'Waiting',
    状态异常: 'Abnormal',
    强制终止中: 'Terminating',
    强制终止成功: 'Terminated',
    强制终止失败: 'Terminate failed',
    确认终止: 'Termination confirmed',
    被丢弃: 'Evicted',
    近1小时: 'Last 1 hour',
    近12小时: 'Last 12 hours',
    近1天: 'Last 24 hours',
    近7天: 'Last 7 days',
    至今: 'until now',
    '确认执行？': 'Are you sure?',
    '该方案未设置全局变量，点击确认将直接执行。': 'Task has no global variables, if confirmed will launch directly.',
    全局变量: 'Global variables',
    执行: 'Launch',
    取消: 'Cancel',
    操作成功: 'Launch successfully',
    导出日志: 'Export',
    步骤内容: 'Step details',
    操作记录: 'Operation logs',
    执行日志: 'Logs',
    变量明细: 'Global vars.',
    查看步骤内容: 'Step details',
    导出日志操作成功: 'Logs export successfully',
    复制: 'Copy',
    字段显示设置: 'Field settings',
    全选: 'All',
    确定: 'OK',
    IP: 'IP',
    '耗时(s)': 'Duration(s)',
    管控区域: 'BK-Net',
    返回码: 'Retcode',
    暂无可复制IP: 'Nothing copied',
    复制成功: 'Copied',
    个: '',
    始: 'S',
    步骤状态: 'Status',
    步骤耗时: 'Duration',
    终: 'E',
    返回编辑: 'R.R',
    人工确认步骤不支持查看步骤详情: 'No details for confirmation steps',
    该步骤还未执行: 'This step has not been runned',
    变量名称: 'Name',
    变量类型: 'Type',
    变量值: 'Value',
    已确认: 'Confirmed',
    确认人: 'Confirm by',
    通知方式: 'Notify by',
    可在此处输入确认或终止的因由: 'You can enter the reason of confirmation here whether it\'s continue or not.',
    结束时间: 'Finished',
    总耗时: 'Total duration',
    全部重试: 'Retry all',
    终止流程: 'STOP',
    重新发起确认: 'Re-confirm',
    确认继续: 'Continue',
    失败IP重试: 'Failed-host retry',
    重试并继续: 'Relaunch',
    强制终止: 'Terminate',
    跳过: 'Skip',
    忽略错误: 'Ignore error',
    进入下一步: 'Go on',
    '确定继续执行？': 'Are you sure?',
    将继续执行后面的步骤: 'Task will continue to run the following steps...',
    '确定终止流程？': 'Are you sure?',
    '人工确认步骤终止后，需「重新发起确认」才可恢复': 'After the confirmation step is stopped, "re-confirm" is required if you want to resume.',
    '确定重新发起确认？': 'Are you sure?',
    将会再次发送消息通知相关的确认人: 'Will send message again to notify the username.',
    '确定全部重试？': 'Are you sure to retry all?',
    '确定失败IP重试？': 'Are you sure?',
    '仅作用于本次执行失败的 IP': 'Step will continue running with retry failed-host.',
    '确定进入下一步？': 'Are you sure?',
    跳过当前步骤进入下一步: 'Ignore the error of current step, and continues to run',
    '确定终止执行任务？': 'Are you sure to terminate the task?',
    终止动作仅对当前还未执行完成的IP有效: 'The termination action is only effect for host that have not yet been finished.',
    '确定重试并继续？': 'Are you sure?',
    '该步骤的所有IP 都将重新执行': 'Will retry all hosts of this step, and keep going.',
    '确定跳过并进入下一步？': 'Sure want to skip and go on ?',
    '将不再等待强制终止动作的结果，直接进入下一步': 'Will no longer wait for the result of the termination, and keep go on.',
    无全局变量: 'There\'s no related global variables',
    时间: 'Datetime',
    操作人: 'Operator',
    动作: 'Action',
    关联步骤: 'Step',
    详情: 'Details',
    正在查看: 'Loading...',
    执行方案: 'Job plan',
    执行脚本: 'Script execution',
    分发文件: 'File transfer',
    人工确认: 'Confirmation',
    步骤: 'Step',
    步骤名称: 'Step name',
    定位到当前步骤: 'Locate to current step',
    开始时间: {
      label: 'Started at',
      colHead: 'Started at',
    },
    任务名称: {
      label: 'Name',
      colHead: 'Name',
    },
    执行方式: {
      label: 'Generated by',
      colHead: 'Generated by',
    },
    任务类型: {
      label: 'Type',
      colHead: 'Type',
    },
    任务状态: {
      label: 'Status',
      colHead: 'Status',
    },
    执行人: {
      label: 'Launched by',
      colHead: 'Launched by',
    },
    执行中: 'Running',
    '按 Esc 即可退出全屏模式': 'Press esc to exit full screen mode',
    还原: 'Normal',
    全屏: 'Full-screen',
    回到顶部: 'Back to top',
    跳至定位点: 'Jump location point',
    前往底部: 'Go to bottom',
    下载日志: 'Download',
    ID只支持数字: 'ID is only allow numbers',
    下载信息: 'Download information',
    上传源信息: 'Upload source information',
    文件名: 'Filename',
    文件大小: 'Size',
    状态: {
      execute: 'Status',
      log: 'State',
    },
    '源服务器 IP': 'Source IP',
    速率: 'Speed',
    进度: 'Progress',
    将覆盖其它条件: 'override other conditions',
    加载中: 'loading',
    更多分组: 'More',
    '目标 IP': 'Target IP',
    执行耗时: 'Exec duration',
    搜索日志: 'Log search',
    '搜索 IP': 'IP search',
    分发文件步骤不支持日志搜索: 'Sorry, file transfer task not support log search',
    分发文件步骤不支持日志导出: 'Sorry, file transfer task not support log export',
    '温馨提示：打包耗时会受到总的日志内容大小影响，请耐心等待': 'REMINDER: packaging time cost will be affected by log content size, please be patient',
    '温馨提示：文件打包中请勿关闭浏览器，以免导致任务中断': 'REMINDER: do not close the browser while packaging',
    '日志文件打包超时，可能因为日志量过大，请选择单台日志下载': 'Package failed! Maybe log is too large or process error, please try later',
    '日志压缩包已准备就绪，': 'Package is ready',
    是否: '',
    直接下载: 'Download now',
    重新打包: 'Re-pack',
    打包中: 'Packaging',
    '打包失败，': 'Package failed',
    重试: 'Retry',
    准备就绪: 'Ready',
    '分组标签长度最大支持256，超过会被自动截断，请留意！': 'The maximum length of the grouping tag is 256. if exceeded, it will be automatically truncated.',
    自动换行: 'Auto-wrap',
    '滚动策略：': 'Strategy: ',
    '滚动机制：': 'Mode: ',
    '默认（执行失败则暂停）': 'Default(pause when fail)',
    '忽略失败，自动滚动下一批': 'Ignore fail, scrolling automatically',
    '不自动，每批次都人工确认': 'Pause after each batch',
    全部批次: 'All',
    确认继续执行: 'Continue to roll',
    跳转至: 'Go to',
    请输入批次: 'Please input the number',
    第: 'The',
    共: 'Total: ',
    批: {
      input: '-th',
      total: 'batches',
      finished: 'finished.',
    },
    已执行: '',
    当前正在显示的内容: 'Currently displayed',
    '复制 IP': 'Copy IP',
    '复制 IPv6': 'Copy IPv6',
    '没有可复制的 IPv4': 'IPv4 address not found',
    '没有可复制的 IPv6': 'IPv6 address not found',
    'IP 与 IPv6 至少需保留一个': 'At least one of IP or IPv6 must be selected',
    容器名称: 'Container name',
    '容器 ID': 'Container ID',
    '所属 Pod 名称': 'Pod name',
    没有可复制的容器名称: 'Container name not found',
  },
};

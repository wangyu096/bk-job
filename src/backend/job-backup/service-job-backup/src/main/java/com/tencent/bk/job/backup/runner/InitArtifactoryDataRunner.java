package com.tencent.bk.job.backup.runner;

import com.tencent.bk.job.backup.config.ArtifactoryConfig;
import com.tencent.bk.job.backup.config.BackupStorageConfig;
import com.tencent.bk.job.common.artifactory.sdk.ArtifactoryHelper;
import com.tencent.bk.job.common.constant.JobConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component("jobBackupInitArtifactoryDataRunner")
public class InitArtifactoryDataRunner implements CommandLineRunner {

    private final ArtifactoryConfig artifactoryConfig;
    private final BackupStorageConfig backupStorageConfig;

    @Autowired
    public InitArtifactoryDataRunner(ArtifactoryConfig artifactoryConfig, BackupStorageConfig backupStorageConfig) {
        this.artifactoryConfig = artifactoryConfig;
        this.backupStorageConfig = backupStorageConfig;
    }

    @Override
    public void run(String... args) {
        if (!JobConstants.FILE_STORAGE_BACKEND_ARTIFACTORY.equals(backupStorageConfig.getStorageBackend())) {
            //不使用制品库作为后端存储时不初始化
            return;
        }
        String baseUrl = artifactoryConfig.getArtifactoryBaseUrl();
        String adminUsername = artifactoryConfig.getArtifactoryAdminUsername();
        String adminPassword = artifactoryConfig.getArtifactoryAdminPassword();
        String jobUsername = artifactoryConfig.getArtifactoryJobUsername();
        String jobPassword = artifactoryConfig.getArtifactoryJobPassword();
        String jobProject = artifactoryConfig.getArtifactoryJobProject();
        String backupRepo = backupStorageConfig.getBackupRepo();
        // 1.检查用户、仓库是否存在
        boolean userRepoExists = ArtifactoryHelper.checkRepoExists(
            baseUrl,
            jobUsername,
            jobPassword,
            jobProject,
            backupRepo
        );
        if (userRepoExists) {
            return;
        }
        // 2.创建项目与用户
        boolean projectUserCreated = ArtifactoryHelper.createJobUserAndProjectIfNotExists(
            baseUrl,
            adminUsername,
            adminPassword,
            jobUsername,
            jobPassword,
            jobProject
        );
        if (!projectUserCreated) {
            log.error(
                "Fail to create project {} or user {}",
                jobProject,
                jobUsername
            );
        }
        // 3.Backup仓库不存在则创建
        String REPO_BACKUP_DESCRIPTION = "BlueKing bk-job official project backup repo," +
            " which is used to save job export data produced by program. " +
            "Do not deleteJobInstanceHotData me unless you know what you are doing";
        boolean repoCreated = ArtifactoryHelper.createRepoIfNotExist(
            baseUrl,
            adminUsername,
            adminPassword,
            jobProject,
            backupRepo,
            REPO_BACKUP_DESCRIPTION
        );
        if (repoCreated) {
            log.info(
                "repo {} created",
                backupRepo
            );
        }
    }

}

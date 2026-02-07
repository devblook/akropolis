/*
 * This file is part of Akropolis
 *
 * Copyright (c) 2025 DevBlook Team and others
 *
 * Akropolis free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akropolis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akropolis. If not, see <http://www.gnu.org/licenses/>.
 */

package me.zetastormy.akropolis.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


public class BackupManager {
    public static final String BACKUP_FOLDER_NAME = "backups";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ssX");

    private final @NotNull Path pluginDataFolderPath;
    private final @NotNull Logger logger;
    private final @NotNull File backupsFolder;

    // Backup state
    private @Nullable File currentBackupFolder = null;
    private boolean isBackupStarted = false;

    public BackupManager(
        final @NotNull Path dataFolderPath,
        final @NotNull Logger logger
    ) throws IOException {
        this.pluginDataFolderPath = dataFolderPath.toAbsolutePath();
        this.logger = logger;

        final Path backupsFolderPath = this.pluginDataFolderPath.resolve(BACKUP_FOLDER_NAME);
        this.backupsFolder = backupsFolderPath.toFile();
        if (this.backupsFolder.mkdirs()) {
            this.logger.info("Successfully created backups folder {}", this.backupsFolder.getAbsolutePath());
        } else {
            this.logger.info("Skipping creating existing backups folder");
        }
    }

    public boolean isBackupStarted() {
        return this.isBackupStarted;
    }

    public void startBackup() throws IllegalStateException {
        if (this.isBackupStarted()) {
            throw new IllegalStateException("Tried to start already started backup");
        }

        final Path folderPath = this.backupsFolder.toPath().resolve("backup-" + FORMATTER.format(ZonedDateTime.now()));

        final File folder = folderPath.toFile();

        this.isBackupStarted = true;
        this.currentBackupFolder = folder;
    }

    public boolean finishBackup() throws IllegalStateException {
        if (!this.isBackupStarted()) {
            throw new IllegalStateException("Tried to stop unknown backup");
        }

        this.isBackupStarted = false;
        this.currentBackupFolder = null;

        return true;
    }

    public boolean backupFile(@NotNull Path filePath) {
        if (!this.isBackupStarted()) {
            logger.error("Tried to backup file while backup has not started");
            return false;
        }

        if (!this.currentBackupFolder.exists()) {
            if (!this.currentBackupFolder.mkdirs()) {
                logger.error("Could not create backup directory {}", currentBackupFolder.toPath());
                return false;
            } else {
                logger.info("Created backup folder {}", currentBackupFolder.toPath());
            }
        }

        filePath = filePath.toAbsolutePath();

        if (!filePath.toFile().exists()) {
            logger.error("Tried to backup inexistent file");
            return false;
        }

        Path relativePath = null;
        try {
            relativePath = this.pluginDataFolderPath.relativize(filePath);
        } catch (final IllegalArgumentException exception) {
            this.logger.error(
                "Could not get relative path between data folder {} and file path {}",
                this.pluginDataFolderPath,
                filePath,
                exception
            );
            return false;
        }

        final Path targetFilePath = this.currentBackupFolder.toPath().toAbsolutePath().resolve(relativePath);

        final File targetFileParentDirectory = targetFilePath.getParent().toFile();
        if (!targetFileParentDirectory.exists() && !targetFileParentDirectory.mkdirs()) {
            logger.error("Could not create parent directories of path {}", targetFilePath);
            return false;
        }

        if (targetFilePath.toFile().exists()) {
            logger.error(
                "Could not backup file {} to target {} because the target already exists",
                filePath,
                targetFilePath.toAbsolutePath()
            );
            return false;
        }

        try {
            Files.copy(filePath, targetFilePath);
        } catch (final IOException exception) {
            logger.error("Could not backup file {} to {}", filePath, targetFilePath, exception);
            return false;
        }


        logger.info(
            "Successfully backed up file {} to {}",
            filePath,
            targetFilePath
        );
        return true;
    }
}

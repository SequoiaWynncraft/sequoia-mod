/*
 * Copyright © sequoia-mod 2025.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package dev.lotnest.sequoia.core.upfixers;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.managers.AccessTokenManager;
import java.io.File;

public final class AccessTokenManagerUpfixer {
    private AccessTokenManagerUpfixer() {}

    public static void fixLegacyFilesIfNeeded() {
        String userFolderPath = AccessTokenManager.getUserSpecificFolderPath();
        File userFolder = new File(userFolderPath);
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }

        File legacyEnvFile = new File(AccessTokenManager.BASE_FOLDER_PATH + AccessTokenManager.ENV_FILE_NAME);
        File legacyTokenFile =
                new File(AccessTokenManager.BASE_FOLDER_PATH + AccessTokenManager.ACCESS_TOKEN_FILE_NAME);

        File newEnvFile = new File(userFolderPath + AccessTokenManager.ENV_FILE_NAME);
        File newTokenFile = new File(userFolderPath + AccessTokenManager.ACCESS_TOKEN_FILE_NAME);

        try {
            if (legacyEnvFile.exists() && !newEnvFile.exists()) {
                if (legacyEnvFile.renameTo(newEnvFile)) {
                    SequoiaMod.debug("Legacy .env file migrated to: " + newEnvFile.getAbsolutePath());
                } else {
                    SequoiaMod.error("Failed to migrate legacy .env file.");
                }
            }

            if (legacyTokenFile.exists() && !newTokenFile.exists()) {
                if (legacyTokenFile.renameTo(newTokenFile)) {
                    SequoiaMod.debug("Legacy access token file migrated to: " + newTokenFile.getAbsolutePath());
                } else {
                    SequoiaMod.error("Failed to migrate legacy access token file.");
                }
            }
        } catch (Exception exception) {
            SequoiaMod.error("Failed to migrate legacy files", exception);
        }
    }
}

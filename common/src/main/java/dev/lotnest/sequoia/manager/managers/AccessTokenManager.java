package dev.lotnest.sequoia.manager.managers;

import dev.lotnest.sequoia.SequoiaMod;
import dev.lotnest.sequoia.utils.EncryptionUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.SecretKey;
import org.apache.commons.lang3.StringUtils;

public final class AccessTokenManager {
    private static final String ACCESS_TOKEN_FILE_PATH = "sequoia" + File.separator + "access_token.properties";
    private static final String ACCESS_TOKEN_KEY = "SequoiaModAccessToken";
    private static final String ENV_FILE_PATH = "sequoia" + File.separator + ".env";
    private static final String ENCRYPTION_KEY_PROPERTY = "SEQUOIA_MOD_ENCRYPTION_KEY";

    private AccessTokenManager() {}

    private static String getEncryptionKey() {
        File envFile = new File(ENV_FILE_PATH);
        if (!envFile.exists()) {
            generateAndStoreEncryptionKey(envFile);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile, StandardCharsets.UTF_8))) {
            String line;
            while (StringUtils.isNotBlank(line = reader.readLine())) {
                if (line.startsWith(ENCRYPTION_KEY_PROPERTY)) {
                    return line.split("=")[1].trim();
                }
            }
        } catch (IOException exception) {
            SequoiaMod.error("Failed to read encryption key from .env file", exception);
        }
        return StringUtils.EMPTY;
    }

    private static void generateAndStoreEncryptionKey(File envFile) {
        File directory = envFile.getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);

        String encryptionKey = Base64.getUrlEncoder().encodeToString(key);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(envFile, StandardCharsets.UTF_8, true))) {
            writer.write(ENCRYPTION_KEY_PROPERTY + "=" + encryptionKey + "\n");
            SequoiaMod.debug(
                    "Generated encryption key: " + encryptionKey + " and stored in " + envFile.getAbsoluteFile());
        } catch (IOException exception) {
            SequoiaMod.error("Failed to write encryption key to .env file", exception);
        }
    }

    public static void storeAccessToken(String token) {
        Properties properties = new Properties();
        File tokenFile = new File(ACCESS_TOKEN_FILE_PATH);

        try {
            String encryptionKey = getEncryptionKey();
            if (StringUtils.isBlank(encryptionKey)) {
                SequoiaMod.error("Encryption key not found in .env file");
                return;
            }

            SecretKey secretKey = EncryptionUtils.getKeyFromString(encryptionKey);
            String encryptedToken = EncryptionUtils.encrypt(token, secretKey);

            String encodedToken =
                    Base64.getUrlEncoder().encodeToString(encryptedToken.getBytes(StandardCharsets.UTF_8));

            if (!tokenFile.exists()) {
                tokenFile.createNewFile();
            }

            try (FileInputStream fileInputStream = new FileInputStream(tokenFile)) {
                properties.load(fileInputStream);
            }

            properties.setProperty(ACCESS_TOKEN_KEY, encodedToken);

            try (FileOutputStream fileOutputStream = new FileOutputStream(tokenFile)) {
                properties.store(fileOutputStream, "Make sure to NEVER share this token with anyone!");
            }
        } catch (Exception exception) {
            SequoiaMod.error("Failed to store access token", exception);
        }
    }

    public static String retrieveAccessToken() {
        Properties properties = new Properties();
        File tokenFile = new File(ACCESS_TOKEN_FILE_PATH);

        try {
            if (tokenFile.exists()) {
                try (FileInputStream fileInputStream = new FileInputStream(tokenFile)) {
                    properties.load(fileInputStream);
                }

                String encodedToken = properties.getProperty(ACCESS_TOKEN_KEY);
                if (StringUtils.isNotBlank(encodedToken)) {
                    String decodedToken =
                            new String(Base64.getUrlDecoder().decode(encodedToken), StandardCharsets.UTF_8);
                    String encryptionKey = getEncryptionKey();

                    if (StringUtils.isNotBlank(encryptionKey)) {
                        SecretKey secretKey = EncryptionUtils.getKeyFromString(encryptionKey);
                        return EncryptionUtils.decrypt(decodedToken, secretKey);
                    }
                }
            }
        } catch (Exception exception) {
            SequoiaMod.error("Failed to retrieve access token", exception);
        }
        return StringUtils.EMPTY;
    }

    public static void invalidateAccessToken() {
        File tokenFile = new File(ACCESS_TOKEN_FILE_PATH);
        if (tokenFile.exists()) {
            tokenFile.delete();
        }
        SequoiaMod.debug("Access token was invalidated.");
    }
}

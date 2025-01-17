package dev.lotnest.sequoia.utils;

import dev.lotnest.sequoia.SequoiaMod;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang3.StringUtils;

public final class EncryptionUtils {
    private static final String ALGORITHM = "AES";

    public static String encrypt(String data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception exception) {
            SequoiaMod.error("Failed to encrypt data", exception);
            return StringUtils.EMPTY;
        }
    }

    public static String decrypt(String encryptedData, Key key) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(decodedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            SequoiaMod.error("Failed to decrypt data", exception);
            return StringUtils.EMPTY;
        }
    }

    public static SecretKey getKeyFromString(String keyString) {
        byte[] decodedKey = Base64.getUrlDecoder().decode(keyString);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }
}

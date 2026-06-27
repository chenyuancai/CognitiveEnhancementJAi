package cn.cyc.ai.cog.common.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM API Key 保护器。
 *
 * <p>存储格式：{@code enc:v1:<base64(iv+ciphertext)>}
 */
public class AesApiKeyProtector implements ApiKeyProtector {

    private static final String PREFIX = "enc:v1:";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private final byte[] keyBytes;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesApiKeyProtector(String masterKey) {
        if (masterKey == null || masterKey.isBlank()) {
            throw new IllegalArgumentException("masterKey 不能为空");
        }
        try {
            this.keyBytes = MessageDigest.getInstance("SHA-256")
                    .digest(masterKey.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("无法初始化 API Key 加密器", exception);
        }
    }

    @Override
    public String protect(String plainApiKey) {
        if (plainApiKey == null || plainApiKey.isBlank()) {
            return plainApiKey;
        }
        if (plainApiKey.startsWith(PREFIX)) {
            return plainApiKey;
        }
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] encrypted = cipher.doFinal(plainApiKey.getBytes(StandardCharsets.UTF_8));
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + encrypted.length);
            buffer.put(iv);
            buffer.put(encrypted);
            return PREFIX + Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception exception) {
            throw new IllegalStateException("API Key 加密失败", exception);
        }
    }

    @Override
    public String reveal(String storedApiKey) {
        if (storedApiKey == null || storedApiKey.isBlank()) {
            return storedApiKey;
        }
        if (!storedApiKey.startsWith(PREFIX)) {
            return storedApiKey;
        }
        try {
            byte[] payload = Base64.getDecoder().decode(storedApiKey.substring(PREFIX.length()));
            ByteBuffer buffer = ByteBuffer.wrap(payload);
            byte[] iv = new byte[GCM_IV_LENGTH];
            buffer.get(iv);
            byte[] encrypted = new byte[buffer.remaining()];
            buffer.get(encrypted);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "AES"), new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new IllegalStateException("API Key 解密失败", exception);
        }
    }
}

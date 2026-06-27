package cn.cyc.ai.cog.file.storage.support;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * MD5 计算辅助。
 */
public final class Md5Support {

    private Md5Support() {
    }

    public static String md5Hex(InputStream inputStream) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            try (DigestInputStream dis = new DigestInputStream(inputStream, digest)) {
                byte[] buffer = new byte[8192];
                while (dis.read(buffer) != -1) {
                    // drain
                }
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("MD5 不可用", exception);
        }
    }
}

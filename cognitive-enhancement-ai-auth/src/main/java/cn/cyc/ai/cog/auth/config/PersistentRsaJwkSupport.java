package cn.cyc.ai.cog.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * 加载或生成 RSA JWK，并持久化到本地文件。
 */
@Component
public class PersistentRsaJwkSupport {

    public JWKSource<SecurityContext> loadOrCreate(AuthJwkProperties properties) {
        RSAKey rsaKey = loadExisting(properties).orElseGet(() -> generateAndPersist(properties));
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    private java.util.Optional<RSAKey> loadExisting(AuthJwkProperties properties) {
        Path path = Path.of(properties.getKeyPath());
        if (!Files.isRegularFile(path)) {
            return java.util.Optional.empty();
        }
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            RSAKey rsaKey = RSAKey.parse(json);
            return java.util.Optional.of(rsaKey);
        } catch (Exception ex) {
            throw new IllegalStateException("读取 JWK 文件失败：" + path, ex);
        }
    }

    private RSAKey generateAndPersist(AuthJwkProperties properties) {
        try {
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            RSAKey rsaKey = new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(UUID.randomUUID().toString())
                    .build();
            Path path = Path.of(properties.getKeyPath());
            Files.createDirectories(path.getParent());
            Files.writeString(path, rsaKey.toJSONString(), StandardCharsets.UTF_8);
            return rsaKey;
        } catch (Exception ex) {
            throw new IllegalStateException("生成并持久化 RSA JWK 失败", ex);
        }
    }
}

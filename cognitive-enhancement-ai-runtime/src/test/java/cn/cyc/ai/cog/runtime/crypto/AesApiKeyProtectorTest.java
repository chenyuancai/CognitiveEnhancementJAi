package cn.cyc.ai.cog.runtime.crypto;

import cn.cyc.ai.cog.common.crypto.AesApiKeyProtector;
import cn.cyc.ai.cog.common.crypto.ApiKeyProtector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AesApiKeyProtectorTest {

    private final ApiKeyProtector protector = new AesApiKeyProtector("test-master-key-for-unit-tests!!");

    @Test
    void shouldEncryptAndDecryptApiKey() {
        String plain = "sk-test-api-key-12345";
        String encrypted = protector.protect(plain);
        assertNotEquals(plain, encrypted);
        assertEquals(plain, protector.reveal(encrypted));
    }

    @Test
    void shouldPassThroughPlaintextLegacyValue() {
        assertEquals("sk-legacy", protector.reveal("sk-legacy"));
    }

    @Test
    void shouldReturnNullWhenTryRevealCannotDecrypt() {
        String encrypted = protector.protect("sk-test-api-key-12345");
        ApiKeyProtector wrongProtector = new AesApiKeyProtector("another-master-key-for-unit-tests!!");

        assertThrows(IllegalStateException.class, () -> wrongProtector.reveal(encrypted));
        assertNull(wrongProtector.tryReveal(encrypted));
    }
}

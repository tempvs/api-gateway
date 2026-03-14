package club.tempvs.gateway.helper;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class CryptoHelper {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private SecretKey secretKey;

    public CryptoHelper(@Value("${crypto.key}") String key) {
        this.secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
    }

    @SneakyThrows
    public String encrypt(String message) {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedMessageBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedMessageBytes);
    }

    @SneakyThrows
    public String decrypt(String message) {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        String normalizedMessage = message.contains("%")
                ? URLDecoder.decode(message, StandardCharsets.UTF_8)
                : message;

        byte[] base64Decoded;
        try {
            base64Decoded = Base64.getDecoder().decode(normalizedMessage);
        } catch (IllegalArgumentException ex) {
            base64Decoded = Base64.getUrlDecoder().decode(normalizedMessage);
        }

        byte[] decryptedMessageBytes = cipher.doFinal(base64Decoded);
        return new String(decryptedMessageBytes);
    }
}

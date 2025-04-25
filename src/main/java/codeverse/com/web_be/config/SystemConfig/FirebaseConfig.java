package codeverse.com.web_be.config.SystemConfig;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.bucket-name}")
    private String bucketName;

    @Value("${firebase.credentials}")
    private String credentials;

    private static final String PASSWORD = "codeverse";

    @PostConstruct
    public void initializeFirebase() {
        try{
            byte[] encryptedData = Files.readAllBytes(new File(credentials).toPath());

            byte[] header = Arrays.copyOfRange(encryptedData, 0, 8);
            if (!new String(header).equals("Salted__")) {
                throw new RuntimeException("File not formatted AES OpenSSL");
            }

            byte[] salt = Arrays.copyOfRange(encryptedData, 8, 16);
            byte[] cipherText = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);

            byte[][] keyAndIV = deriveKeyAndIV(PASSWORD.getBytes(), salt);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            byte[] decrypted = cipher.doFinal(cipherText);
            InputStream serviceAccount = new ByteArrayInputStream(decrypted);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket(bucketName)
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }

    private static byte[][] deriveKeyAndIV(byte[] password, byte[] salt) throws Exception {
        final int keyLength = 32;
        final int ivLength = 16;
        final int totalLength = keyLength + ivLength;

        PBEKeySpec spec = new PBEKeySpec(new String(password).toCharArray(), salt, 10000, totalLength * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyAndIV = skf.generateSecret(spec).getEncoded();

        byte[] key = Arrays.copyOfRange(keyAndIV, 0, keyLength);
        byte[] iv = Arrays.copyOfRange(keyAndIV, keyLength, totalLength);
        return new byte[][]{key, iv};
    }

}

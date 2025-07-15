package codeverse.com.web_be.config.SystemConfig;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.bucket-name}")
    private String bucketName;

//     @Value("${firebase.credentials}")
//     private String credentials;

    @PostConstruct
    public void initializeFirebase() {
        try {
            String base64Config = System.getenv("FIREBASE_CONFIG_BASE64");
            if (base64Config == null || base64Config.isEmpty()) {
                throw new RuntimeException("FIREBASE_CONFIG_BASE64 not set");
            }
            byte[] encryptedData = Base64.getDecoder().decode(base64Config);
            InputStream serviceAccount = new ByteArrayInputStream(encryptedData);

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

//    @PostConstruct
//    public void initializeFirebase() {
//        try {
//            byte[] encryptedData = Base64.getDecoder().decode(credentials);
//            InputStream serviceAccount = new ByteArrayInputStream(encryptedData);
//
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .setStorageBucket(bucketName)
//                    .build();
//            if (FirebaseApp.getApps().isEmpty()) {
//                FirebaseApp.initializeApp(options);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to initialize Firebase", e);
//        }
//    }

}

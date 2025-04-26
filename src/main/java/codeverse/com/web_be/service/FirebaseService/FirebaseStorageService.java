package codeverse.com.web_be.service.FirebaseService;

import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class FirebaseStorageService {
    @Value("${firebase.bucket-name}")
    private String bucketName;

    private static final String IMAGE_PREFIX = "images/";
    private static final String VIDEO_PREFIX = "videos/";

    public String uploadFile(MultipartFile file, String folder) {
        try{
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String fullPath = folder + fileName;

            StorageClient.getInstance().bucket().create(fullPath, file.getInputStream(), file.getContentType());

            return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucketName, fullPath.replace("/", "%2F"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public void validateImage(MultipartFile file) {
        validateFile(file, "image/");
    }

    public void validateVideo(MultipartFile file) {
        validateFile(file, "video/");
    }

    private void validateFile(MultipartFile file, String prefix) {
        if(file.isEmpty()){
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if(contentType == null || !contentType.startsWith(prefix)){
            throw new IllegalArgumentException(prefix + " file is required");
        }
    }

    public String uploadImage(MultipartFile file) {
        validateImage(file);
        return uploadFile(file, IMAGE_PREFIX);
    }

    public String uploadVideo(MultipartFile file) {
        validateVideo(file);
        return uploadFile(file, VIDEO_PREFIX);
    }
}

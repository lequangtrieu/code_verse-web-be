package codeverse.com.web_be.service.FirebaseService;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FirebaseStorageService {
    private final Storage storage;

    @Value("${firebase.bucket-name}")
    private String bucketName;

    private static final String IMAGE_PREFIX = "images/";
    private static final String VIDEO_PREFIX = "videos/";
    private static final String HTML_PREFIX = "theories/";


    public FirebaseStorageService() {
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

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

    public String uploadHtml(MultipartFile file) {
        return uploadFile(file, HTML_PREFIX);
    }

    public String downloadHtml(String url) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download HTML from Firebase URL: " + url, e);
        }
    }

    public void cleanUpUnusedMedia(Set<String> previousMediaUrls, Set<String> currentMediaUrls) {
        Set<String> unusedUrls = previousMediaUrls.stream()
                .filter(url -> !currentMediaUrls.contains(url))
                .collect(Collectors.toSet());

        for (String mediaUrl : unusedUrls) {
            try {
                String decodedUrl = URLDecoder.decode(mediaUrl, StandardCharsets.UTF_8);
                String path = extractPathFromUrl(decodedUrl);

                if (path != null && path.startsWith("editor/")) {
                    boolean deleted = storage.delete(BlobId.of(bucketName, path));
                    if (deleted) {
                        System.out.println("Deleted unused media: " + path);
                    } else {
                        System.out.println("Failed to delete: " + path);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error deleting media: " + mediaUrl);
                e.printStackTrace();
            }
        }
    }

    private String extractPathFromUrl(String url) {
        int index = url.indexOf("/o/");
        if (index == -1) return null;

        String partial = url.substring(index + 3);
        int queryIndex = partial.indexOf("?");
        if (queryIndex != -1) {
            partial = partial.substring(0, queryIndex);
        }

        return partial.replace("%2F", "/");
    }


}

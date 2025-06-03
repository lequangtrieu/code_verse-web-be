package codeverse.com.web_be.service.FirebaseService;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class FirebaseStorageService {
    @Value("${firebase.bucket-name}")
    private String bucketName;

    private static final String IMAGE_PREFIX = "images/";
    private static final String HTML_PREFIX = "theories/";

    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String fullPath = folder + fileName;

            StorageClient.getInstance().bucket().create(fullPath, file.getInputStream(), file.getContentType());

            return generatePublicUrl(fullPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public void validateImage(MultipartFile file) {
        validateFile(file, "image/");
    }

    private void validateFile(MultipartFile file, String prefix) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(prefix)) {
            throw new IllegalArgumentException(prefix + " file is required");
        }
    }

    public String uploadImage(MultipartFile file) {
        validateImage(file);
        return uploadFile(file, IMAGE_PREFIX);
    }

    public void cleanUpUnusedMedia(Set<String> previousMediaUrls, Set<String> currentMediaUrls) {
        Set<String> unused = previousMediaUrls.stream()
                .filter(url -> !currentMediaUrls.contains(url))
                .map(this::extractPathFromUrl)
                .collect(Collectors.toSet());

        Bucket bucket = StorageClient.getInstance().bucket();

        unused.removeAll(currentMediaUrls.stream().map(this::extractPathFromUrl).collect(Collectors.toSet()));

        for (String media : unused) {
            try {
                boolean deleted = bucket.get(media).delete();
                if (!deleted) {
                    System.out.println("Could not delete media: " + media);
                }
            } catch (Exception e) {
                System.err.println("Error deleting media: " + media);
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

        return URLDecoder.decode(partial.replace("%2F", "/"), StandardCharsets.UTF_8);
    }

    public Set<String> listAllMediaInFolder(String folderPath) {
        Set<String> mediaUrls = new HashSet<>();

        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(folderPath));

            for (Blob blob : blobs.iterateAll()) {
                if (!blob.isDirectory()) {

                    String downloadUrl = generatePublicUrl(blob.getName());
                    mediaUrls.add(downloadUrl);

                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to list media in folder: " + folderPath, e);
        }

        return mediaUrls;
    }

    private String generatePublicUrl(String filePath) {
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucketName, filePath.replace("/", "%2F"));
    }

    public String downloadHtmlByPath(String url) {
        try {
            String storagePath = extractPathFromUrl(url);
            Bucket bucket = StorageClient.getInstance().bucket();
            Blob blob = bucket.get(storagePath);
            if (blob == null) throw new RuntimeException("File not found: " + storagePath);

            byte[] content = blob.getContent();
            return new String(content, StandardCharsets.UTF_8);
        } catch (StorageException e) {
            throw new RuntimeException("Failed to download HTML from Firebase: " + e.getMessage(), e);
        }
    }

    public void deleteOldHtmlVersions(Long id, String excludeUrl) {
        String folder = HTML_PREFIX + id + "/";
        Bucket bucket = StorageClient.getInstance().bucket();
        String excludePath = extractPathFromUrl(excludeUrl);

        for (Blob blob : bucket.list(Storage.BlobListOption.prefix(folder)).iterateAll()) {
            String path = blob.getName();
            if (!path.equals(excludePath)) {
                try {
                    blob.delete();
                    System.out.println("Deleted old html file: " + path);
                } catch (Exception e) {
                    System.out.println("Failed to delete old html file: " + path + " - " + e.getMessage());
                }
            }
        }
    }

}

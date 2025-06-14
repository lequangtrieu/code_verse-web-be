package codeverse.com.web_be.service.TheoryService;

import codeverse.com.web_be.dto.request.TheoryRequest.TheoryCreateRequest;
import codeverse.com.web_be.dto.response.TheoryResponse.TheoryResponse;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.entity.Theory;
import codeverse.com.web_be.mapper.TheoryMapper;
import codeverse.com.web_be.repository.LessonRepository;
import codeverse.com.web_be.repository.TheoryRepository;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.FirebaseService.HtmlMediaExtractor;
import codeverse.com.web_be.service.GenericServiceImpl;
import io.netty.util.ResourceLeakException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import java.nio.charset.StandardCharsets;

@Service
public class TheoryServiceImpl extends GenericServiceImpl<Theory, Long> implements ITheoryService {
    private final TheoryRepository theoryRepository;
    private final LessonRepository lessonRepository;
    private final FirebaseStorageService firebaseStorageService;

    protected TheoryServiceImpl(TheoryRepository theoryRepository,
                                LessonRepository lessonRepository,
                                FirebaseStorageService firebaseStorageService) {
        super(theoryRepository);
        this.theoryRepository = theoryRepository;
        this.lessonRepository = lessonRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Override
    public TheoryResponse saveTheory(TheoryCreateRequest request) {
        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        final String[] htmlHolder = new String[1];
        String newHtmlUrl = null;

        if (request.getContentFile() != null && !request.getContentFile().isEmpty()) {
            try {
                htmlHolder[0] = new String(request.getContentFile().getBytes(), StandardCharsets.UTF_8);
                newHtmlUrl = firebaseStorageService.uploadFile(request.getContentFile(), "theories/" + lesson.getId() + "/");
                firebaseStorageService.deleteOldHtmlVersions(lesson.getId(), newHtmlUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read HTML content file", e);
            }
        }

        Optional<Theory> optionalOldTheory = theoryRepository.findByLesson(lesson);
        optionalOldTheory.ifPresent(oldTheory -> {
            try {
                String oldHtmlContent = firebaseStorageService.downloadHtmlByPath(oldTheory.getContent());
                Set<String> oldMedia = HtmlMediaExtractor.extractMediaUrlsFromHtml(oldHtmlContent);
                Set<String> newMedia = HtmlMediaExtractor.extractMediaUrlsFromHtml(htmlHolder[0]);
                firebaseStorageService.cleanUpUnusedMedia(oldMedia, newMedia);
            } catch (Exception e) {
                System.out.println("Failed to clean up unused media: " + e.getMessage());
            }
        });

        try {
            String folder = "editor/" + lesson.getId() + "/";
            Set<String> allUploadedMedia = firebaseStorageService.listAllMediaInFolder(folder);
            Set<String> newMedia = HtmlMediaExtractor.extractMediaUrlsFromHtml(htmlHolder[0]);
            firebaseStorageService.cleanUpUnusedMedia(allUploadedMedia, newMedia);
        } catch (Exception e) {
            System.out.println("Failed to clean up unused uploaded media: " + e.getMessage());
        }

        Theory theory = optionalOldTheory.orElse(new Theory());
        theory.setLesson(lesson);
        theory.setTitle(request.getTitle());
        theory.setContent(newHtmlUrl);

        return TheoryResponse.fromEntity(theoryRepository.save(theory));
    }
}

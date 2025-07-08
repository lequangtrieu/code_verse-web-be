package codeverse.com.web_be.service.TheoryService;

import codeverse.com.web_be.dto.request.TheoryRequest.TheoryCreateRequest;
import codeverse.com.web_be.dto.response.TheoryResponse.TheoryResponse;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.entity.Theory;
import codeverse.com.web_be.repository.LessonRepository;
import codeverse.com.web_be.repository.TheoryRepository;
import codeverse.com.web_be.service.FirebaseService.FirebaseStorageService;
import codeverse.com.web_be.service.FirebaseService.HtmlMediaExtractor;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

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

        String htmlContent = request.getContent();

        Optional<Theory> optionalOldTheory = theoryRepository.findByLesson(lesson);
        optionalOldTheory.ifPresent(oldTheory -> {
            try {
                String oldHtmlContent = oldTheory.getContent();
                Set<String> oldMedia = HtmlMediaExtractor.extractMediaUrlsFromHtml(oldHtmlContent);
                Set<String> newMedia = HtmlMediaExtractor.extractMediaUrlsFromHtml(htmlContent);
                firebaseStorageService.cleanUpUnusedMedia(oldMedia, newMedia);
            } catch (Exception e) {
                System.out.println("Failed to clean up unused media: " + e.getMessage());
            }
        });

        try {
            String folder = "editor/" + lesson.getId() + "/";
            Set<String> allUploadedMedia = firebaseStorageService.listAllMediaInFolder(folder);
            Set<String> newMedia = HtmlMediaExtractor.extractMediaUrlsFromHtml(htmlContent);
            firebaseStorageService.cleanUpUnusedMedia(allUploadedMedia, newMedia);
        } catch (Exception e) {
            System.out.println("Failed to clean up unused uploaded media: " + e.getMessage());
        }

        Theory theory = optionalOldTheory.orElse(new Theory());
        theory.setLesson(lesson);
        theory.setTitle(request.getTitle());
        theory.setContent(htmlContent);

        return TheoryResponse.fromEntity(theoryRepository.save(theory));
    }

    @Override
    public TheoryResponse getTheoryByLessonId(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(
                () -> new ResourceNotFoundException("Lesson not found")
        );

        Optional<Theory> optionalTheory = theoryRepository.findByLesson(lesson);
        Theory theory = optionalTheory.orElse(null);

        return TheoryResponse.fromEntity(theory);
    }
}

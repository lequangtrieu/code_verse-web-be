package codeverse.com.web_be.service.CourseModuleService;

import codeverse.com.web_be.dto.response.LessonResponse.LessonWithinMaterialResponse;
import codeverse.com.web_be.dto.response.MaterialSectionResponse.MaterialSectionForUpdateResponse;
import codeverse.com.web_be.dto.response.MaterialSectionResponse.MaterialSectionResponse;
import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.repository.LessonRepository;
import codeverse.com.web_be.repository.CourseModuleRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseModuleServiceImpl extends GenericServiceImpl<CourseModule, Long> implements ICourseModuleService {

    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;

    protected CourseModuleServiceImpl(CourseModuleRepository courseModuleRepository,
                                      LessonRepository lessonRepository) {
        super(courseModuleRepository);
        this.courseModuleRepository = courseModuleRepository;
        this.lessonRepository = lessonRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<MaterialSectionForUpdateResponse> getMaterialSectionListByCourseId(Long courseId) {
        List<CourseModule> sections = courseModuleRepository.findByCourseId(courseId);
        return sections.stream()
                .map(section -> {
                    List<Lesson> lessons = lessonRepository.findByMaterialSectionId(section.getId());

                    return MaterialSectionForUpdateResponse.builder()
                            .id(section.getId())
                            .title(section.getTitle())
                            .orderIndex(section.getOrderIndex())
                            .previewable(section.isPreviewable())
                            .lessons(lessons.stream()
                                    .map(LessonWithinMaterialResponse::fromEntity)
                                    .toList())
                            .build();
                })
                .toList();
    }

    @Override
    public MaterialSectionResponse getById(Long id) {
        CourseModule courseModule = courseModuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material section not found"));
        return MaterialSectionResponse.fromEntity(courseModule);
    }
}

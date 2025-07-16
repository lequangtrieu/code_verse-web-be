package codeverse.com.web_be.service.CourseModuleService;

import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseModuleMoreInfoDTO;
import codeverse.com.web_be.dto.response.LessonResponse.LessonResponse;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleResponse;
import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.repository.LessonRepository;
import codeverse.com.web_be.repository.CourseModuleRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import codeverse.com.web_be.service.LessonService.ILessonService;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseModuleServiceImpl extends GenericServiceImpl<CourseModule, Long> implements ICourseModuleService {
    private final ILessonService lessonService;
    private final CourseModuleRepository courseModuleRepository;
    private final LessonRepository lessonRepository;

    protected CourseModuleServiceImpl(ILessonService lessonService,
                                      CourseModuleRepository courseModuleRepository,
                                      LessonRepository lessonRepository) {
        super(courseModuleRepository);
        this.lessonService = lessonService;
        this.courseModuleRepository = courseModuleRepository;
        this.lessonRepository = lessonRepository;
    }

    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    @Override
    public List<CourseModuleResponse> getCourseModuleListByCourseId(Long courseId) {
        List<CourseModule> sections = courseModuleRepository.findByCourseId(courseId);
        return sections.stream()
                .map(module -> {
                    List<Lesson> lessons = lessonRepository.findByCourseModuleId(module.getId());

                    return CourseModuleResponse.builder()
                            .id(module.getId())
                            .title(module.getTitle())
                            .orderIndex(module.getOrderIndex())
                            .lessons(lessons.stream()
                                    .map(LessonResponse::fromEntity)
                                    .toList())
                            .createdAt(module.getCreatedAt())
                            .updatedAt(module.getUpdatedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public CourseModuleResponse getById(Long id) {
        CourseModule courseModule = courseModuleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Material section not found"));
        return CourseModuleResponse.fromEntity(courseModule);
    }

    @Override
    public void deleteModule(Long moduleId) {
        CourseModule courseModule = courseModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Material section not found"));
        List<Lesson> lessons = lessonRepository.findByCourseModuleId(courseModule.getId());
        if(!lessons.isEmpty()){
            for (Lesson lesson : lessons) {
                lessonService.deleteLesson(lesson.getId());
            }
        }
        courseModuleRepository.deleteById(courseModule.getId());
    }

    @Override
    public List<CourseModuleMoreInfoDTO> getCourseModuleMoreInfoDTOList(Long courseId) {
        return null;
    }
}

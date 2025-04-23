package codeverse.com.web_be.service.CourseService;

import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.repository.CourseRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl extends GenericServiceImpl<Course, Long> implements ICourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        super(courseRepository);
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> findByInstructorId(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAllByIsDeletedFalseAndIsPublishedTrue()
                .stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
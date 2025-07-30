package codeverse.com.web_be.service.CourseService;

import codeverse.com.web_be.dto.request.CodeRequest.CodeRequestDTO;
import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.request.CourseRequest.CourseUpdateRequest;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleValidationResponse;
import codeverse.com.web_be.dto.response.CourseResponse.*;
import codeverse.com.web_be.dto.response.CourseResponse.Course.SimpleCourseCardDto;
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseDetailResponse;
import codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse.LearnerResponse;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.service.IGenericService;
import jakarta.mail.MessagingException;

import java.math.BigDecimal;
import java.util.List;

public interface ICourseService extends IGenericService<Course, Long> {
    List<Course> findByInstructorId(Long instructorId);

    List<Course> findByInstructorUsername(String username);
    List<Course> findTrainingByInstructor();
    TrainingResponse findTrainingById(Long id);
    void updateTraining(Long id, CourseCreateRequest request);

    List<CourseResponse> getAllCourses();

    Course createCourse(CourseCreateRequest request);

    Course updateCourse(Long id, CourseUpdateRequest request);
    List<LearnerResponse> getLearnersByCourseId (Long courseId);

    List<CourseResponse> getCoursesByLearnerId(Long userId);

    List<CourseProgressResponse> getInProgressCoursesByLearnerId(Long userId);

    List<CourseProgressResponse> getCompletedCoursesByLearnerId(Long userId);

    List<CourseResponse> getSuggestedCoursesByLearnerId(Long userId);

    CourseDetailResponse getCourseById(Long courseId);

    List<CourseProgressResponse> getAllCoursesByLearnerId(Long userId);

    List<CourseForUpdateResponse> getAllCoursesByAdmin();

    CourseModuleValidationResponse validateCourseSection(Long courseId);

    void updateCourseStatus(Long courseId, CourseUpdateRequest request);
    void updateCourseDiscount(Long courseId, BigDecimal discount);

    CourseDetailDTO getCourseDetails(Long courseId, Long userId);

    String submitCodeHandler(CodeRequestDTO request) throws MessagingException;

    List<SimpleCourseCardDto> getAuthorCourses(Long instructorId, Long excludedCourseId);

    List<SimpleCourseCardDto> getPopularCourses();
}
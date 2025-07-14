package codeverse.com.web_be.service.CourseService;

import codeverse.com.web_be.dto.request.CodeRequest.CodeRequestDTO;
import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.request.CourseRequest.CourseUpdateRequest;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleValidationResponse;
import codeverse.com.web_be.dto.response.CourseResponse.Course.SimpleCourseCardDto;
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseDetailResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetailDTO;
import codeverse.com.web_be.dto.response.CourseResponse.CourseForUpdateResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;
import codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse.LearnerResponse;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.service.IGenericService;
import jakarta.mail.MessagingException;

import java.util.List;

public interface ICourseService extends IGenericService<Course, Long> {
    List<Course> findByInstructorId(Long instructorId);

    List<Course> findByInstructorUsername(String username);

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

    CourseDetailDTO getCourseDetails(Long courseId, Long userId);

    String submitCodeHandler(CodeRequestDTO request) throws MessagingException;

    List<SimpleCourseCardDto> getAuthorCourses(Long instructorId, Long excludedCourseId);

    List<SimpleCourseCardDto> getPopularCourses();
}
package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.CourseEnrollmentResponse.CertificateInfoDTO;
import codeverse.com.web_be.dto.response.CourseEnrollmentResponse.CompletedCourseDTO;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.CourseEnrollment;
import codeverse.com.web_be.service.CourseEnrollmentService.ICourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment")
@RequiredArgsConstructor
public class CourseEnrollmentController {
    private final ICourseEnrollmentService courseEnrollmentService;

    @GetMapping("/completion")
    public ResponseEntity<ApiResponse<Float>> getCompletionPercentage(
            @RequestParam Long courseId,
            @RequestParam Long userId
    ) {
        Float percent = courseEnrollmentService.getUserCompletionPercentage(userId, courseId);

        ApiResponse<Float> response = ApiResponse.<Float>builder()
                .result(percent)
                .message("Get Completion Percentage Success")
                .code(HttpStatus.OK.value())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed")
    public ResponseEntity<?> getCompletedCourses(@RequestParam Long userId) {
        List<CourseEnrollment> completedCourses = courseEnrollmentService.getCompletedCoursesByUserId(userId);

        List<CompletedCourseDTO> response = completedCourses.stream()
                .map(ce -> new CompletedCourseDTO(
                        ce.getCourse().getId(),
                        ce.getCourse().getTitle(),
                        ce.getCourse().getInstructor().getUsername(),
                        ce.getCompletionPercentage(),
                        ce.getUpdatedAt(),
                        ce.getUser().getUsername()
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/certificate")
    public ResponseEntity<?> getCertificateInfo(
            @RequestParam Long userId,
            @RequestParam Long courseId
    ) {
        CertificateInfoDTO dto = courseEnrollmentService.getCertificateInfo(userId, courseId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/is-enrolled")
    public ResponseEntity<Boolean> isUserEnrolled(
            @RequestParam Long courseId,
            @RequestParam String username
    ) {
        boolean isEnrolled = courseEnrollmentService.isUserEnrolled(courseId, username);
        return ResponseEntity.ok(isEnrolled);
    }
}

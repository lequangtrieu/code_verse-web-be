package codeverse.com.web_be.dto.response.CourseEnrollmentResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CertificateInfoDTO {
    private String studentName;
    private String courseName;
    private String instructorName;
    private LocalDateTime completionDate;
}

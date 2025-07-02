package codeverse.com.web_be.dto.response.CourseEnrollmentResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CourseEnrollmentStatusDTO {
    private boolean enrolled;
    private float completionPercentage;
}

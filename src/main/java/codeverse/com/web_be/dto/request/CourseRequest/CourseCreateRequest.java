package codeverse.com.web_be.dto.request.CourseRequest;

import codeverse.com.web_be.enums.CourseLevel;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCreateRequest {
    private String title;
    private String description;
    private Long categoryId;
    private Long instructorId;
    private MultipartFile imageFile;
    private CourseLevel level;
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;
}

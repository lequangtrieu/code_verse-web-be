package codeverse.com.web_be.dto.request.CourseRequest;

import codeverse.com.web_be.enums.CodeLanguage;
import codeverse.com.web_be.enums.CourseLevel;
import codeverse.com.web_be.enums.CourseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseUpdateRequest {
    private String title;
    private String description;
    private Long categoryId;
    private MultipartFile imageFile;
    private String thumbnailUrl;
    private CourseLevel level;
    private CodeLanguage language;
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;
    @Builder.Default
    private CourseStatus status = CourseStatus.DRAFT;
    @Builder.Default
    private Boolean isDeleted = false;
}

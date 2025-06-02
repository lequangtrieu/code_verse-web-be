package codeverse.com.web_be.dto.request.CourseRequest;

import codeverse.com.web_be.dto.request.CourseModuleRequest.CourseModuleFullCreateRequest;
import codeverse.com.web_be.enums.CodeLanguage;
import codeverse.com.web_be.enums.CourseLevel;
import codeverse.com.web_be.enums.CourseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseCreateRequest {
    private String title;
    private String description;
    private Long categoryId;
    private String instructor;
    private MultipartFile imageFile;
    private CourseLevel level;
    private CodeLanguage language;
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;
    @Builder.Default
    private CourseStatus status = CourseStatus.DRAFT;

    private List<CourseModuleFullCreateRequest> modules;
}

package codeverse.com.web_be.dto.request.CourseRequest;

import codeverse.com.web_be.dto.request.MaterialSectionRequest.MaterialSectionFullCreateRequest;
import codeverse.com.web_be.enums.CourseLevel;
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
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;
    private Boolean isPublished = false;

    private List<MaterialSectionFullCreateRequest> modules;
}

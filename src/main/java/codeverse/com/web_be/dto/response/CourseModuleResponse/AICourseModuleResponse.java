package codeverse.com.web_be.dto.response.CourseModuleResponse;

import codeverse.com.web_be.dto.request.CourseModuleRequest.CourseModuleCreateRequest;
import codeverse.com.web_be.dto.request.LessonRequest.LessonCreateRequest;
import lombok.Data;

import java.util.List;

@Data
public class AICourseModuleResponse extends CourseModuleCreateRequest {
    private List<LessonCreateRequest> subLessons;
}

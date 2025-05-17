package codeverse.com.web_be.service.CourseModuleService;

import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleForUpdateResponse;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleResponse;
import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface ICourseModuleService extends IGenericService<CourseModule, Long> {
    List<CourseModuleForUpdateResponse> getCourseModuleListByCourseId(Long courseId);
    CourseModuleResponse getById(Long id);
}

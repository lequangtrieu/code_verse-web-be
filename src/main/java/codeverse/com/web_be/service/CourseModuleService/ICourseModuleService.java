package codeverse.com.web_be.service.CourseModuleService;

import codeverse.com.web_be.dto.response.MaterialSectionResponse.MaterialSectionForUpdateResponse;
import codeverse.com.web_be.dto.response.MaterialSectionResponse.MaterialSectionResponse;
import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface ICourseModuleService extends IGenericService<CourseModule, Long> {
    List<MaterialSectionForUpdateResponse> getMaterialSectionListByCourseId(Long courseId);
    MaterialSectionResponse getById(Long id);
}

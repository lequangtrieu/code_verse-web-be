package codeverse.com.web_be.service.CourseModuleService;

import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseModuleMoreInfoDTO;
import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface ICourseModuleService extends IGenericService<CourseModule, Long> {
    List<CourseModuleResponse> getCourseModuleListByCourseId(Long courseId);
    CourseModuleResponse getById(Long id);
    void deleteModule(Long moduleId);
    List<CourseModuleMoreInfoDTO> getCourseModuleMoreInfoDTOList(Long courseId);
}

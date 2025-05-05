package codeverse.com.web_be.service.MaterialSectionService;

import codeverse.com.web_be.dto.response.MaterialSectionResponse.MaterialSectionForUpdateResponse;
import codeverse.com.web_be.dto.response.MaterialSectionResponse.MaterialSectionResponse;
import codeverse.com.web_be.entity.MaterialSection;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface IMaterialSectionService extends IGenericService<MaterialSection, Long> {
    List<MaterialSectionForUpdateResponse> getMaterialSectionListByCourseId(Long courseId);
    MaterialSectionResponse getById(Long id);
}

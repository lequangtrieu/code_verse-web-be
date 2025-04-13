package codeverse.com.web_be.service.CourseMaterialService;

import codeverse.com.web_be.entity.CourseMaterial;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface ICourseMaterialService extends IGenericService<CourseMaterial, Long> {
    List<CourseMaterial> findByCourseId(Long courseId);
}
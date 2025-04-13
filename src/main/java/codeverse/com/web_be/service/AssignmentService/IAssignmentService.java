package codeverse.com.web_be.service.AssignmentService;

import codeverse.com.web_be.entity.Assignment;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface IAssignmentService extends IGenericService<Assignment, Long> {
    List<Assignment> findByCourseId(Long courseId);
}
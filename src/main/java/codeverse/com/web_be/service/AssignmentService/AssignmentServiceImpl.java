package codeverse.com.web_be.service.AssignmentService;

import codeverse.com.web_be.entity.Assignment;
import codeverse.com.web_be.repository.AssignmentRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignmentServiceImpl extends GenericServiceImpl<Assignment, Long> implements IAssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository) {
        super(assignmentRepository);
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public List<Assignment> findByCourseId(Long courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }
}
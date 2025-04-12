package codeverse.com.web_be.service.CourseMaterialService;

import codeverse.com.web_be.entity.CourseMaterial;
import codeverse.com.web_be.repository.CourseMaterialRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseMaterialServiceImpl extends GenericServiceImpl<CourseMaterial, Long> implements ICourseMaterialService {

    private final CourseMaterialRepository courseMaterialRepository;

    public CourseMaterialServiceImpl(CourseMaterialRepository courseMaterialRepository) {
        super(courseMaterialRepository);
        this.courseMaterialRepository = courseMaterialRepository;
    }

    @Override
    public List<CourseMaterial> findByCourseId(Long courseId) {
        return courseMaterialRepository.findByCourseId(courseId);
    }
}
package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.MaterialSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialSectionRepository extends JpaRepository<MaterialSection, Long> {
    List<MaterialSection> findByCourseId(Long courseId);
    MaterialSection findById(long id);
}

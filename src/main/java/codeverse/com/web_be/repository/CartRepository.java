package codeverse.com.web_be.repository;

import codeverse.com.web_be.entity.Cart;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    int countByUser(User user);
    List<Cart> findAllByUser(User user);
    void deleteByUser(User user);
    boolean existsByUserAndCourse(User user, Course course);
    void deleteByUserAndCourse(User user, Course course);
    void deleteByUserAndCourseIdIn(User user, List<Long> courseIds);
    Optional<Cart> findByUserAndCourse(User user, Course course);
}
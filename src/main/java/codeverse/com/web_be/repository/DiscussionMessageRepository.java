package codeverse.com.web_be.repository;


import codeverse.com.web_be.entity.DiscussionMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussionMessageRepository extends JpaRepository<DiscussionMessage, Long> {
    List<DiscussionMessage> findAllByLessonIdAndParentMessageIsNullOrderByCreatedAtDesc(Long lessonId);
}

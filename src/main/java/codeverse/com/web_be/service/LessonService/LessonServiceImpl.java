package codeverse.com.web_be.service.LessonService;

import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.repository.LessonRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class LessonServiceImpl extends GenericServiceImpl<Lesson, Long> implements ILessonService{
    private final LessonRepository lessonRepository;

    protected LessonServiceImpl(LessonRepository lessonRepository) {
        super(lessonRepository);
        this.lessonRepository = lessonRepository;
    }
}

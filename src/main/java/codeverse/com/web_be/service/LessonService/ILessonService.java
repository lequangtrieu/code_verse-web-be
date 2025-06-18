package codeverse.com.web_be.service.LessonService;

import codeverse.com.web_be.dto.request.LessonRequest.LessonCreateRequest;
import codeverse.com.web_be.dto.response.LessonResponse.LessonResponse;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.service.IGenericService;

public interface ILessonService extends IGenericService<Lesson, Long> {
    LessonResponse createLesson(LessonCreateRequest request);
    LessonResponse updateLesson(Long lessonId, LessonCreateRequest request);
    void deleteLesson(Long lessonId);
}

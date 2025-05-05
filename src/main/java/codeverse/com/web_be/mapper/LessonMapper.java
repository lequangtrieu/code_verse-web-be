package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.LessonRequest.LessonCreateRequest;
import codeverse.com.web_be.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "materialSection", ignore = true)
    @Mapping(target = "theory", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lesson lessonCreateRequestToLesson(LessonCreateRequest request);
}

package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.LessonRequest.LessonCreateRequest;
import codeverse.com.web_be.dto.request.LessonRequest.LessonUpdateRequest;
import codeverse.com.web_be.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface LessonMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courseModule", ignore = true)
    @Mapping(target = "theory", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lesson lessonCreateRequestToLesson(LessonCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courseModule", ignore = true)
    @Mapping(target = "theory", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lesson lessonUpdateRequestToLesson(LessonUpdateRequest request);

    @Mapping(target = "courseModule", ignore = true)
    @Mapping(target = "theory", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    default void updateLessonFromRequest(LessonUpdateRequest request, @MappingTarget Lesson lesson){
        if(!Objects.equals(lesson.getTitle(), request.getTitle())){
            lesson.setTitle(request.getTitle());
        }
        if (!Objects.equals(lesson.getDuration(), request.getDuration())){
            lesson.setDuration(request.getDuration());
        }
        if(!Objects.equals(lesson.getOrderIndex(), request.getOrderIndex())){
            lesson.setOrderIndex(request.getOrderIndex());
        }
    }
}

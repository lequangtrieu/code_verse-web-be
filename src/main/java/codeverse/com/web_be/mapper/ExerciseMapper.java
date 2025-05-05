package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseCreateRequest;
import codeverse.com.web_be.entity.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Exercise exerciseCreateRequestToExercise(ExerciseCreateRequest request);
}

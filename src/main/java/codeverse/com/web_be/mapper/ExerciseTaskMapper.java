package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseTaskCreateRequest;
import codeverse.com.web_be.entity.ExerciseTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExerciseTaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ExerciseTask exerciseTaskCreateRequestToExerciseTask(ExerciseTaskCreateRequest request);
}

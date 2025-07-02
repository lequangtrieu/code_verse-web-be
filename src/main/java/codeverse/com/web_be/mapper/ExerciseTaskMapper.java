package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseTaskCreateRequest;
import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseTaskUpdateRequest;
import codeverse.com.web_be.entity.ExerciseTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface ExerciseTaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ExerciseTask exerciseTaskCreateRequestToExerciseTask(ExerciseTaskCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ExerciseTask exerciseTaskUpdateRequestToExerciseTask(ExerciseTaskUpdateRequest request);

    @Mapping(target = "exercise", ignore = true)
    void updateExerciseTaskFromRequest(ExerciseTaskUpdateRequest request, @MappingTarget ExerciseTask exerciseTask);
}

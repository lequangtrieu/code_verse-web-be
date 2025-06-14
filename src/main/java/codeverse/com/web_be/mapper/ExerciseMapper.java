package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseCreateRequest;
import codeverse.com.web_be.dto.request.ExerciseRequest.ExerciseUpdateRequest;
import codeverse.com.web_be.entity.Exercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Exercise exerciseCreateRequestToExercise(ExerciseCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Exercise exerciseUpdateRequestToExercise(ExerciseUpdateRequest request);

    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    default void updateExerciseFromRequest(ExerciseUpdateRequest request, @MappingTarget Exercise exercise){
        if (!Objects.equals(exercise.getTitle(), request.getTitle())) {
            exercise.setTitle(request.getTitle());
        }
        if (!Objects.equals(exercise.getExpReward(), request.getExpReward())) {
            exercise.setExpReward(request.getExpReward());
        }
        if (!Objects.equals(exercise.getInstruction(), request.getInstruction())) {
            exercise.setInstruction(request.getInstruction());
        }
    }
}

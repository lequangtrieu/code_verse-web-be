package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.TheoryRequest.TheoryCreateRequest;
import codeverse.com.web_be.dto.request.TheoryRequest.TheoryUpdateRequest;
import codeverse.com.web_be.entity.Theory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface TheoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Theory theoryCreateRequestToTheory(TheoryCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Theory theoryUpdateRequestToTheory(TheoryUpdateRequest request);

    @Mapping(target = "lesson", ignore = true)
    default void updateTheoryFromRequest(TheoryUpdateRequest request, @MappingTarget Theory theory){
        if(!Objects.equals(theory.getTitle(), request.getTitle())){
            theory.setTitle(request.getTitle());
        }
        if (!Objects.equals(theory.getContent(), request.getContent())) {
            theory.setContent(request.getContent());
        }
    }
}

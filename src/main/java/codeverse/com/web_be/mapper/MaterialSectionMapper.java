package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.MaterialSectionRequest.MaterialSectionCreateRequest;
import codeverse.com.web_be.dto.request.MaterialSectionRequest.MaterialSectionUpdateRequest;
import codeverse.com.web_be.entity.CourseModule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface MaterialSectionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CourseModule materialSectionCreateRequestToMaterialSection(MaterialSectionCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CourseModule materialSectionUpdateRequestToMaterialSection(MaterialSectionUpdateRequest request);

    @Mapping(target = "course", ignore = true)
    default void updateMaterialSectionFromRequest(MaterialSectionUpdateRequest request, @MappingTarget CourseModule courseModule){
        if(!Objects.equals(courseModule.getOrderIndex(), request.getOrderIndex())){
            courseModule.setOrderIndex(request.getOrderIndex());
        }
        if (!Objects.equals(courseModule.getTitle(), request.getTitle())){
            courseModule.setTitle(request.getTitle());
        }
        if (!Objects.equals(courseModule.isPreviewable(), request.isPreviewable())){
            courseModule.setPreviewable(request.isPreviewable());
        }
    }
}

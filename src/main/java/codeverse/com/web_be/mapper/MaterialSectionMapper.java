package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.MaterialSectionRequest.MaterialSectionCreateRequest;
import codeverse.com.web_be.dto.request.MaterialSectionRequest.MaterialSectionUpdateRequest;
import codeverse.com.web_be.entity.MaterialSection;
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
    MaterialSection materialSectionCreateRequestToMaterialSection(MaterialSectionCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MaterialSection materialSectionUpdateRequestToMaterialSection(MaterialSectionUpdateRequest request);

    @Mapping(target = "course", ignore = true)
    default void updateMaterialSectionFromRequest(MaterialSectionUpdateRequest request, @MappingTarget MaterialSection materialSection){
        if(!Objects.equals(materialSection.getOrderIndex(), request.getOrderIndex())){
            materialSection.setOrderIndex(request.getOrderIndex());
        }
        if (!Objects.equals(materialSection.getTitle(), request.getTitle())){
            materialSection.setTitle(request.getTitle());
        }
        if (!Objects.equals(materialSection.isPreviewable(), request.isPreviewable())){
            materialSection.setPreviewable(request.isPreviewable());
        }
    }
}

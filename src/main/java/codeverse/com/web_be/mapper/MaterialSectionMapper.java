package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.MaterialSectionRequest.MaterialSectionCreateRequest;
import codeverse.com.web_be.entity.MaterialSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MaterialSectionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MaterialSection materialSectionCreateRequestToMaterialSection(MaterialSectionCreateRequest request);
}

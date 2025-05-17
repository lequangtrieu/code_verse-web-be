package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.CourseModuleRequest.CourseModuleCreateRequest;
import codeverse.com.web_be.dto.request.CourseModuleRequest.CourseModuleUpdateRequest;
import codeverse.com.web_be.entity.CourseModule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface CourseModuleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CourseModule courseModuleCreateRequestToCourseModule(CourseModuleCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CourseModule courseModuleUpdateRequestToCourseModule(CourseModuleUpdateRequest request);

    @Mapping(target = "course", ignore = true)
    default void updateCourseModuleFromRequest(CourseModuleUpdateRequest request, @MappingTarget CourseModule courseModule){
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

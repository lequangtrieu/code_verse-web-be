package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.CourseRequest.CourseCreateRequest;
import codeverse.com.web_be.dto.response.CourseResponse.CourseForUpdateResponse;
import codeverse.com.web_be.dto.response.CourseResponse.CourseResponse;
import codeverse.com.web_be.entity.Category;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", source = "course.description")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "instructor", source = "instructor")
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Course courseCreateRequestToCourse(CourseCreateRequest course, Category category, User instructor);

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "instructor", source = "instructor.name")
    CourseResponse courseToCourseResponse(Course course);

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "instructor", source = "instructor.name")
    @Mapping(target = "modules", ignore = true)
    CourseForUpdateResponse  courseToCourseForUpdateResponse(Course course);
}

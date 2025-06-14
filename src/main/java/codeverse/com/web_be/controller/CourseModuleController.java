package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.CourseModuleRequest.CourseModuleCreateRequest;
import codeverse.com.web_be.dto.request.CourseModuleRequest.CourseModuleUpdateRequest;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleForUpdateResponse;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.mapper.CourseModuleMapper;
import codeverse.com.web_be.service.CourseService.ICourseService;
import codeverse.com.web_be.service.CourseModuleService.ICourseModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/module")
@RequiredArgsConstructor
public class CourseModuleController {
    private final ICourseModuleService courseModuleService;
    private final ICourseService courseService;
    private final CourseModuleMapper courseModuleMapper;

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<CourseModuleForUpdateResponse>> getCourseModuleListByCourseId(@PathVariable Long courseId) {
        List<CourseModuleForUpdateResponse> materials = courseModuleService.getCourseModuleListByCourseId(courseId);
        return ApiResponse.<List<CourseModuleForUpdateResponse>>builder()
                .result(materials)
                .code(HttpStatus.OK.value())
                .build();
    }

    @PostMapping
    public ApiResponse<CourseModuleResponse> createCourseModule(@RequestBody CourseModuleCreateRequest request){
        CourseModule courseModule = courseModuleMapper.courseModuleCreateRequestToCourseModule(request);
        Course course = courseService.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        courseModule.setCourse(course);
        CourseModule createdCourseModule = courseModuleService.save(courseModule);
        return ApiResponse.<CourseModuleResponse>builder()
                .result(CourseModuleResponse.fromEntity(createdCourseModule))
                .code(HttpStatus.CREATED.value())
                .build();
    }

    @PutMapping("/{courseModuleId}")
    public ApiResponse<CourseModuleResponse> updateCourseModule(@PathVariable Long courseModuleId, @RequestBody CourseModuleCreateRequest request){
        CourseModule courseModule = courseModuleService.findById(courseModuleId)
                .orElseThrow(() -> new ResourceNotFoundException("Course module not found"));
        courseModuleMapper.updateCourseModuleFromRequest(request,courseModule);
        CourseModule updatedCourseModule = courseModuleService.update(courseModule);
        return ApiResponse.<CourseModuleResponse>builder()
                .result(CourseModuleResponse.fromEntity(updatedCourseModule))
                .code(HttpStatus.OK.value())
                .build();
    }
}

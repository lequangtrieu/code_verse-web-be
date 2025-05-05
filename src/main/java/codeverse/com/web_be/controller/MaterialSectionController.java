package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.MaterialSectionRequest.MaterialSectionCreateRequest;
import codeverse.com.web_be.dto.response.MaterialSectionResponse.MaterialSectionForUpdateResponse;
import codeverse.com.web_be.dto.response.MaterialSectionResponse.MaterialSectionResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.MaterialSection;
import codeverse.com.web_be.mapper.MaterialSectionMapper;
import codeverse.com.web_be.service.CourseService.ICourseService;
import codeverse.com.web_be.service.MaterialSectionService.IMaterialSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material")
@RequiredArgsConstructor
public class MaterialSectionController {
    private final IMaterialSectionService materialSectionService;
    private final ICourseService courseService;
    private final MaterialSectionMapper materialSectionMapper;

    @GetMapping("/course/{courseId}")
    public ApiResponse<List<MaterialSectionForUpdateResponse>> getMaterialSectionListByCourseId(@PathVariable Long courseId) {
        List<MaterialSectionForUpdateResponse> materials = materialSectionService.getMaterialSectionListByCourseId(courseId);
        return ApiResponse.<List<MaterialSectionForUpdateResponse>>builder()
                .result(materials)
                .code(HttpStatus.OK.value())
                .build();
    }

    @PostMapping
    public ApiResponse<MaterialSectionResponse> createMaterialSection(@RequestBody MaterialSectionCreateRequest request){
        MaterialSection materialSection = materialSectionMapper.materialSectionCreateRequestToMaterialSection(request);
        Course course = courseService.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        materialSection.setCourse(course);
        MaterialSection createdMaterialSection = materialSectionService.save(materialSection);
        return ApiResponse.<MaterialSectionResponse>builder()
                .result(MaterialSectionResponse.fromEntity(createdMaterialSection))
                .code(HttpStatus.CREATED.value())
                .build();
    }
}

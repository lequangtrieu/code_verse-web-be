package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.TestCaseRequest.TestCaseCreateRequest;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.dto.response.TestCaseResponse.TestCaseResponse;
import codeverse.com.web_be.entity.Exercise;
import codeverse.com.web_be.entity.TestCase;
import codeverse.com.web_be.mapper.TestCaseMapper;
import codeverse.com.web_be.service.ExerciseService.IExerciseService;
import codeverse.com.web_be.service.TestCaseService.ITestCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-case")
@RequiredArgsConstructor
public class TestCaseController {
    private final IExerciseService exerciseService;
    private final ITestCaseService testCaseService;
    private final TestCaseMapper testCaseMapper;

    @PostMapping
    public ApiResponse<TestCaseResponse> createTestCase(@RequestBody TestCaseCreateRequest request) {
        TestCase testCase = testCaseMapper.testCaseCreateRequestToTestCase(request);
        Exercise exercise = exerciseService.findById(request.getExerciseId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found"));
        testCase.setExercise(exercise);
        TestCase createdTestCase = testCaseService.save(testCase);
        return ApiResponse.<TestCaseResponse>builder()
                .result(TestCaseResponse.fromEntity(createdTestCase))
                .code(HttpStatus.CREATED.value())
                .build();
    }
}

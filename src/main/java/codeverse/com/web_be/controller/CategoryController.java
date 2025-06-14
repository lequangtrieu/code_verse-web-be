package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.CategoryResponse.CategoryResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.service.CategoryService.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryServiceImpl categoryService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.ok(
                ApiResponse.<List<CategoryResponse>>builder()
                        .result(categoryService.getAllCategories())
                        .message("Success")
                        .build()
        );
    }

}

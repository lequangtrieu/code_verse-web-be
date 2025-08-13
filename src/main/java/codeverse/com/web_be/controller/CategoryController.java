package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.CategoryRequest.CategoryRequest;
import codeverse.com.web_be.dto.response.CategoryResponse.CategoryResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.service.CategoryService.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @PutMapping("/{id}/delete")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}

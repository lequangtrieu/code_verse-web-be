package codeverse.com.web_be.service.CategoryService;

import codeverse.com.web_be.dto.response.CategoryResponse.CategoryResponse;
import codeverse.com.web_be.entity.Category;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface ICategoryService extends IGenericService<Category, Long> {
    List<CategoryResponse> getAllCategories();
}
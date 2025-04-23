package codeverse.com.web_be.service.CategoryService;

import codeverse.com.web_be.dto.response.CategoryResponse.CategoryResponse;
import codeverse.com.web_be.entity.Category;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.repository.CategoryRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends GenericServiceImpl<Category, Long> implements ICategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByIsDeletedFalse()
                .stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}

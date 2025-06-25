package codeverse.service

import codeverse.com.web_be.controller.CategoryController
import codeverse.com.web_be.entity.Category
import codeverse.com.web_be.repository.CategoryRepository
import codeverse.com.web_be.service.CategoryService.CategoryServiceImpl
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CategoryControllerSpec extends Specification {

    def categoryRepository = Mock(CategoryRepository)
    def categoryService = new CategoryServiceImpl(categoryRepository)
    def categoryController = new CategoryController(categoryService)

    def "No: #no → getAllCategories returns #expectedSize result(s) when repo returns #inputSize categories"() {
        given:
        def mockCategories = buildCategories(inputSize)
        categoryRepository.findAllByIsDeletedFalse() >> mockCategories

        when:
        ResponseEntity response = categoryController.getAllCategories()
        def apiResponse = response.body
        def resultList = apiResponse.result

        then:
        response.statusCode.value() == 200
        resultList.size() == expectedSize
        resultList*.name == mockCategories*.name

        where:
        no       | inputSize || expectedSize
        "case01" | 0         || 0
        "case02" | 1         || 1
        "case03" | 3         || 3
    }

    private static List<Category> buildCategories(int count) {
        return (0..<count).collect { i ->
            Category.builder()
                    .id((long) (i + 1))
                    .name("Category ${i + 1}")
                    .isDeleted(false)
                    .build()
        }
    }
}

package codeverse.com.web_be.mapper;

import codeverse.com.web_be.dto.request.TestCaseRequest.TestCaseCreateRequest;
import codeverse.com.web_be.entity.TestCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestCaseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    TestCase testCaseCreateRequestToTestCase(TestCaseCreateRequest testCaseCreateRequest);
}

package codeverse.com.web_be.service.TheoryService;

import codeverse.com.web_be.dto.request.TheoryRequest.TheoryCreateRequest;
import codeverse.com.web_be.dto.response.TheoryResponse.TheoryResponse;
import codeverse.com.web_be.entity.Theory;
import codeverse.com.web_be.service.IGenericService;

public interface ITheoryService extends IGenericService<Theory, Long> {
    TheoryResponse createTheory(TheoryCreateRequest request);
}

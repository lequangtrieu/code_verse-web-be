package codeverse.com.web_be.service.TestCaseService;

import codeverse.com.web_be.entity.TestCase;
import codeverse.com.web_be.service.IGenericService;

import java.util.List;

public interface ITestCaseService extends IGenericService<TestCase, Long> {
    List<TestCase> findByAssignmentId(Long assignmentId);
}
package codeverse.com.web_be.service.TestCaseService;

import codeverse.com.web_be.entity.TestCase;
import codeverse.com.web_be.repository.TestCaseRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TestCaseServiceImpl extends GenericServiceImpl<TestCase, Long> implements ITestCaseService {
    private final TestCaseRepository testCaseRepository;

    public TestCaseServiceImpl(TestCaseRepository testCaseRepository) {
        super(testCaseRepository);
        this.testCaseRepository = testCaseRepository;
    }
}

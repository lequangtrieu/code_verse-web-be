package codeverse.com.web_be.service.TheoryService;

import codeverse.com.web_be.entity.Theory;
import codeverse.com.web_be.repository.TheoryRepository;
import codeverse.com.web_be.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TheoryServiceImpl extends GenericServiceImpl<Theory, Long> implements ITheoryService {
    private final TheoryRepository theoryRepository;

    protected TheoryServiceImpl(TheoryRepository theoryRepository) {
        super(theoryRepository);
        this.theoryRepository = theoryRepository;
    }
}

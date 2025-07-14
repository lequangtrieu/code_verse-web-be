package codeverse.com.web_be.service.UserService;

import codeverse.com.web_be.dto.response.WithdrawalResponse.InstructorIncomeDTO;
import codeverse.com.web_be.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstructorIncomeService {

    private final OrderRepository orderRepository;

    public List<InstructorIncomeDTO> getInstructorIncome(Long instructorId) {
        return orderRepository.getInstructorIncome(instructorId);
    }
}

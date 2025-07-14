package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.WithdrawalResponse.InstructorIncomeDTO;
import codeverse.com.web_be.service.UserService.InstructorIncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instructors/{instructorId}/income")
@RequiredArgsConstructor
public class InstructorIncomeController {

    private final InstructorIncomeService instructorIncomeService;

    @GetMapping
    public ResponseEntity<List<InstructorIncomeDTO>> getInstructorIncome(
            @PathVariable("instructorId") Long instructorId) {
        List<InstructorIncomeDTO> incomeList = instructorIncomeService.getInstructorIncome(instructorId);
        return ResponseEntity.ok(incomeList);
    }
}


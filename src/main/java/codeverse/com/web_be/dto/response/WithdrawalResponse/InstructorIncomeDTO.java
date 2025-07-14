package codeverse.com.web_be.dto.response.WithdrawalResponse;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InstructorIncomeDTO {
    private Long courseId;
    private String courseTitle;
    private String learner;
    private BigDecimal amount;
    private LocalDateTime date;

    public InstructorIncomeDTO(Long courseId, String courseTitle, String learner, BigDecimal amount, LocalDateTime date) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.learner = learner;
        this.amount = amount;
        this.date = date;
    }
}

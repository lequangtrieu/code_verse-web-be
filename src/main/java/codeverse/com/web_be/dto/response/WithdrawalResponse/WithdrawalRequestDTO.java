package codeverse.com.web_be.dto.response.WithdrawalResponse;

import codeverse.com.web_be.enums.WithdrawalStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WithdrawalRequestDTO {
    private Long id;
    private BigDecimal amount;
    private WithdrawalStatus status;
    private String adminNote;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
}

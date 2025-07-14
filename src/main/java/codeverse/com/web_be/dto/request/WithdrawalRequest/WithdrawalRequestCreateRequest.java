package codeverse.com.web_be.dto.request.WithdrawalRequest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequestCreateRequest {
    private BigDecimal amount;
}

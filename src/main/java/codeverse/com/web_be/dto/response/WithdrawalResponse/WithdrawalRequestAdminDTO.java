package codeverse.com.web_be.dto.response.WithdrawalResponse;


import codeverse.com.web_be.enums.WithdrawalStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalRequestAdminDTO {
    private Long id;
    private Long instructorId;
    private String instructorName;
    private String qrCodeUrl;
    private BigDecimal amount;
    private String paymentMethod;
    private WithdrawalStatus status;
    private String adminNote;
    private LocalDateTime createdAt;
}
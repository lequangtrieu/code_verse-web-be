package codeverse.com.web_be.dto.request.AddToCartRequest;

import lombok.Data;

@Data
public class PaymentConfirmationRequest {
    private String status;
    private Long orderId;
    private String username;
}

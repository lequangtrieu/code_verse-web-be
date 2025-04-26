package codeverse.com.web_be.dto.response.CheckoutResponse;

import codeverse.com.web_be.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CheckoutResponse {
    private String checkoutUrl;
}

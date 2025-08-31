package codeverse.com.web_be.dto.request.AddToCartRequest;
import lombok.Data;

import java.util.List;

@Data
public class AddToCartRequest {
    private String username;
    private Long courseId;
    private List<Long> selectedCartItemId;
}
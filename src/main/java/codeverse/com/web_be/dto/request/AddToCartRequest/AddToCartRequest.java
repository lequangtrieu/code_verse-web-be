package codeverse.com.web_be.dto.request.AddToCartRequest;
import lombok.Data;

@Data
public class AddToCartRequest {
    private String username;
    private Long courseId;
}
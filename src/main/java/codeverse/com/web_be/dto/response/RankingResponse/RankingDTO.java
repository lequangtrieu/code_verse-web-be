package codeverse.com.web_be.dto.response.RankingResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankingDTO {
    private Long userId;
    private String username;
    private String avatar;
    private Long totalExp;
}

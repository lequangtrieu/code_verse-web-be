package codeverse.com.web_be.dto.response.DashboardResponse;

import codeverse.com.web_be.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRoleStatDTO {
    private UserRole role;
    private long count;
}

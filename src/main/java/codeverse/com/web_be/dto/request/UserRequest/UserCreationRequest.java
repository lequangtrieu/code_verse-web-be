package codeverse.com.web_be.dto.request.UserRequest;

import codeverse.com.web_be.dto.common.CodeVerseCreateModel;
import codeverse.com.web_be.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest extends CodeVerseCreateModel {
    String username;
    String password;
    UserRole role = UserRole.LEARNER;
}

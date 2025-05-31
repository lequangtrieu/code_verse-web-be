package codeverse.com.web_be.service.FunctionHelper;

import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FunctionHelper {
    private final UserRepository userRepository;

    public User getActiveUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getIsDeleted()) {
            throw new AppException(ErrorCode.USER_BANNED);
        }

        return user;
    }
}

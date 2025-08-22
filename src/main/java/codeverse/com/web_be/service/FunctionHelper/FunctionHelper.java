package codeverse.com.web_be.service.FunctionHelper;

import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public List<String> parseInputStringToList(String inputString) {
        if (inputString == null || inputString.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile("#@ip!(.*?)#@ip!");
        Matcher matcher = pattern.matcher(inputString);

        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        return result;
    }
}

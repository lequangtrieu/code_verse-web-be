package codeverse.com.web_be.service.FunctionHelper;

import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.exception.AppException;
import codeverse.com.web_be.exception.ErrorCode;
import codeverse.com.web_be.repository.UserRepository;
import codeverse.com.web_be.service.AIService.GroqService;
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
    private final GroqService groqService;

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

    public boolean isOffensive(String text) {
        if (text == null || text.isBlank()) return false;

        String systemPrompt = """
                You are a strict global content moderation filter for a learning platform.
                Task: Detect if the text contains ANY offensive, vulgar, hateful, racist,
                sexually explicit, violent, discriminatory, abusive, or meaningless spam content.
                
                Rules:
                - ANY vulgar or swear word in ANY language (e.g. "cặc", "cức", "fuck", "shit") → OFFENSIVE.
                - Random meaningless gibberish (e.g. "ádasds", "!!!!", "xxxxxx") → OFFENSIVE.
                - Repeated characters that are still part of a meaningful sentence (e.g. "tuyệt vời quá điiiiii", "so coooool!") → CLEAN.
                - Normal sentences, even with uppercase or punctuation → CLEAN.
                - Only neutral, polite, meaningful text → CLEAN.
                
                Output must be exactly one word: OFFENSIVE or CLEAN
                """;

        String userPrompt = "Review text:\n" + text;

        String result = groqService.chat(systemPrompt, userPrompt, 0.0);
        return result != null && result.trim().equalsIgnoreCase("OFFENSIVE");
    }
}

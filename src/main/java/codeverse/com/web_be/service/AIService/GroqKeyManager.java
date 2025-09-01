package codeverse.com.web_be.service.AIService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class GroqKeyManager {

    private final List<String> apiKeys;
    private final AtomicInteger index = new AtomicInteger(0);

    public GroqKeyManager(@Value("${GROQ_API_KEYS}") String keys) {
        this.apiKeys = Arrays.asList(keys.split(","));
    }

    public String getCurrentKey() {
        return apiKeys.get(index.get());
    }

    public void switchKey() {
        int next = (index.incrementAndGet()) % apiKeys.size();
        index.set(next);
    }

    public int getKeysCount() {
        return apiKeys.size();
    }
}

package codeverse.com.web_be.service.AIService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroqService {

    private final GroqKeyManager keyManager;

    @Value("${ai.groq.model:llama-3.3-70b-versatile}")
    private String groqModel;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String chat(String systemPrompt, String userPrompt, Double temperature) {
        RestTemplate rt = new RestTemplate();

        Map<String, Object> payload = Map.of(
                "model", groqModel,
                "temperature", temperature == null ? 0.2 : temperature,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        for (int attempt = 0; attempt < keyManager.getKeysCount(); attempt++) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(keyManager.getCurrentKey());

            HttpEntity<Map<String, Object>> req = new HttpEntity<>(payload, headers);

            try {
                ResponseEntity<Map> res = rt.postForEntity(GROQ_URL, req, Map.class);
                Map body = res.getBody();
                if (body == null) return "";

                List<Map> choices = (List<Map>) body.get("choices");
                if (choices == null || choices.isEmpty()) return "";

                Map first = choices.get(0);
                Map msg = (Map) first.get("message");
                if (msg == null) return "";
                return (String) msg.getOrDefault("content", "");
            } catch (HttpClientErrorException.TooManyRequests e) {
                keyManager.switchKey();
            } catch (Exception e) {
                throw e;
            }
        }
        return "All API keys exhausted.";
    }
}

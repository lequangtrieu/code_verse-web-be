package codeverse.com.web_be.service.AIService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroqService {

    private String groqApiKey = System.getenv("GROQ_API_KEY");

    @Value("${ai.groq.model:llama3-70b-8192}")
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> res = rt.postForEntity(GROQ_URL, req, Map.class);

        Map body = res.getBody();
        if (body == null) return "";
        List<Map> choices = (List<Map>) body.get("choices");
        if (choices == null || choices.isEmpty()) return "";

        Map first = choices.get(0);
        Map msg = (Map) first.get("message");
        if (msg == null) return "";
        return (String) msg.getOrDefault("content", "");
    }
}

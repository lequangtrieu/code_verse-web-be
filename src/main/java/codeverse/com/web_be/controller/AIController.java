package codeverse.com.web_be.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {
    private final String GROQ_API_KEY = "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";

    @PostMapping("/feedback")
    public ResponseEntity<?> getAIFeedback(@RequestBody Map<String, String> body) {
        String prompt = generatePrompt(body);

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> groqRequest = Map.of(
                "model", "llama3-70b-8192",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a programming assistant helping learners understand and fix their code."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(GROQ_API_KEY);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(groqRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.groq.com/openai/v1/chat/completions", request, Map.class
        );

        Map<String, Object> contentMap = (Map<String, Object>) ((Map<String, Object>)
                ((List<?>) response.getBody().get("choices")).get(0)).get("message");

        return ResponseEntity.ok(Map.of("suggestion", contentMap.get("content")));
    }

    private String generatePrompt(Map<String, String> body) {
        String exerciseTitle = body.getOrDefault("exerciseTitle", "");
        String exerciseTasks = body.getOrDefault("exerciseTasks", "");

        return String.format("""
                        I am building an online code learning platform where learners write code in an online editor.
                        The code is executed using the JDoodle API (a cloud-based compiler), and we test it using specific test cases.
                        
                        Each coding exercise comes with a description and a list of required tasks that the learner must fulfill.
                        
                        Exercise title:
                        %s
                        
                        Tasks to complete:
                        %s
                        
                        One of the test cases has failed during code execution.
                        
                        ---------------------
                        Programming language: %s
                        Learner's code:
                        %s
                        ---------------------
                        
                        Test case details:
                        Input:
                        %s
                        
                        Expected output:
                        %s
                        
                        Actual output:
                        %s
                        
                        Please help analyze why this test case failed, and suggest how to fix the code.
                        Make sure your explanation is clear and targeted for beginners.
                        If possible, provide a corrected version of the code.
                        """,
                exerciseTitle,
                exerciseTasks,
                body.get("language"),
                body.get("code"),
                body.get("input"),
                body.get("expected"),
                body.get("actual")
        );
    }
}

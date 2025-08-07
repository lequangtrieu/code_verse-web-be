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
    private final String GROQ_API_KEY = System.getenv("GROQ_API_KEY");


    @PostMapping("/feedback")
    public ResponseEntity<?> getAIFeedback(@RequestBody Map<String, Object> body) {
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
                "https://api.groq.com/openai/v1/chat/completions",
                request,
                Map.class
        );

        Map<String, Object> contentMap = (Map<String, Object>)
                ((Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0)).get("message");

        return ResponseEntity.ok(Map.of("suggestion", contentMap.get("content")));
    }

    private String generatePrompt(Map<String, Object> body) {
        String exerciseTitle = (String) body.getOrDefault("exerciseTitle", "");
        String exerciseTasks = (String) body.getOrDefault("exerciseTasks", "");
        String exerciseDescription = (String) body.getOrDefault("exerciseDescription", "");
        String language = (String) body.getOrDefault("language", "");
        String code = (String) body.getOrDefault("code", "");
        String input = (String) body.getOrDefault("input", "");
        String expected = (String) body.getOrDefault("expected", "");
        String actual = (String) body.getOrDefault("actual", "");

        return String.format("""
            You are an AI assistant integrated into **CodeVerse**, an online platform where learners solve coding challenges directly in the browser.

            🧪 Our system uses **JDoodle**, a stateless cloud-based compiler that runs code with:
            • Raw input (via standard input methods only)
            • Raw output (compared exactly to expected output)

            ⚠️ IMPORTANT: JDoodle does **not** support interactive prompts. Therefore:
            • ❌ DO NOT suggest any prompt-based code like `print("Enter your name:")`, `cout << "Input:"`, etc.
            • ✅ Learners MUST read input using proper standard input syntax for their language (e.g., `Scanner` in Java, `input()` in Python, `cin` in C++).
            • ✅ Learners MUST produce output that **exactly matches** the expected output, without extra print statements or comments.
            • ❌ DO NOT hardcode input values like `a = 8` or `if (a == 12)` — code must work for ANY input.

            ---------------------
            🎯 Exercise Title:
            %s

            📄 Description:
            %s

            ✅ Required Tasks:
            %s

            ---------------------
            💻 Language: %s

            👨‍🎓 Learner's Submitted Code:
            %s

            🧪 Test Case:
            • Input: %s
            • Expected Output: %s
            • Actual Output: %s

            ---------------------
            🔍 This test case FAILED — your task is to help the learner fix the problem.

            Please act as a **strict AI code reviewer** and follow these rules:

            1. Determine if the code is valid:
               - ✅ If it's algorithmic, uses input-reading correctly, and produces correct output → respond with:
                 [RESULT]: PASS
               - ❌ If it contains hardcoded logic (e.g., `a = 5`, `if (a == 12)`), extra prompts, or only works for specific inputs → respond with:
                 [RESULT]: FAIL

            2. After the [RESULT], clearly explain WHY the code passes or fails.

            3. If the result is FAIL, you MUST provide a corrected version of the code using a code block (```), and ensure that:
               • It reads input properly based on the language (e.g., `Scanner`, `input()`, `cin`)
               • It works for ANY valid input
               • It has NO extra prompts or unnecessary output

            4. If the result is PASS, you MAY show a clean version of the code again (optional).

            🛑 Your response MUST start with:
            [RESULT]: PASS
            or
            [RESULT]: FAIL

            Be clear, consistent, and always follow the platform's execution rules.
            """,
                exerciseTitle,
                exerciseDescription,
                exerciseTasks,
                language,
                code,
                input,
                expected,
                actual
        );
    }
}

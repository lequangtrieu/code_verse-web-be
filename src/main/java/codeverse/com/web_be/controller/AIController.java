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

        String testCaseSection = (input.isBlank() && expected.isBlank() && actual.isBlank())
                ? "🧪 This exercise does not include explicit input/output test cases. Review the code based on the description and required tasks only. Do not fabricate inputs or outputs."
                : String.format("""
                🧪 Test Case:
                • Input: %s
                • Expected Output: %s
                • Actual Output: %s
                """, input, expected, actual);

        String reviewContext = (input.isBlank() && expected.isBlank() && actual.isBlank())
                ? "ℹ️ There may be no explicit I/O for this exercise. Evaluate the submission strictly against the problem description and required tasks."
                : "🔍 The above test case FAILED in our judge. Help the learner fix the problem.";

        return String.format("""
                        You are an AI assistant integrated into **CodeVerse**, an online platform where learners solve coding challenges directly in the browser.
                        
                        🧪 Our system uses **JDoodle**, a stateless cloud-based compiler that runs code with:
                        • Raw input (via standard input methods only)
                        • Raw output (compared exactly to expected output)
                        
                        ⚠️ IMPORTANT platform rules (apply to **all languages**):
                        • ✅ Learners MUST read input using the standard input mechanism of the language (e.g., `Scanner` in Java, `input()` in Python, `cin` in C++, reading stdin in Node.js).
                        • ❌ DO NOT suggest interactive prompts like `print("Enter your name:")`, `cout << "Input:"`, etc.
                        • ❌ DO NOT add extra print statements or comments in the output; it must match expected output **exactly** (including newlines and spacing).
                        • ❌ DO NOT hardcode input values (e.g., `a = 8`, `if (a == 12)`), or branch on specific known inputs.
                        • ✅ Valid solutions must generalize and work for ANY valid input (when inputs exist).
                        
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
                        
                        %s
                        
                        ---------------------
                        %s
                        
                        Please act as a **strict AI code reviewer** and follow these rules:
                        
                        1) Decide if the submission is valid:
                           - ✅ If it is algorithmic, uses the correct input-reading method for the language (when inputs exist), and produces the correct output format → respond with:
                             [RESULT]: PASS
                           - ❌ If it contains hardcoded logic, extra prompts/prints, or only works for specific inputs → respond with:
                             [RESULT]: FAIL
                        
                        2) After the [RESULT], clearly explain WHY the code passes or fails (be concise and beginner-friendly).
                        
                        3) If the result is FAIL, you MUST provide a corrected version in a code block (```), ensuring:
                           • It uses the standard input method when inputs exist
                           • It works for ANY valid input
                           • It prints only the required output (no extra text)
                        
                        4) If the result is PASS, you MAY include a cleaned version (optional).
                        
                        🛑 Your response MUST start with exactly one of:
                        [RESULT]: PASS
                        or
                        [RESULT]: FAIL
                        
                        Always follow these platform rules and avoid ambiguity.
                        """,
                exerciseTitle,
                exerciseDescription,
                exerciseTasks,
                language,
                code,
                testCaseSection,
                reviewContext
        );
    }
}

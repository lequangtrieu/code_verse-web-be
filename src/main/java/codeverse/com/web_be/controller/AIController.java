package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.AISummaryRequest.AISummaryRequest;
import codeverse.com.web_be.dto.request.AISummaryRequest.AISummaryResponse;
import codeverse.com.web_be.service.AIService.DeepgramService;
import codeverse.com.web_be.service.AIService.GroqService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
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
    private final DeepgramService deepgramService;
    private final GroqService groqService;

    @PostMapping("/feedback")
    public ResponseEntity<?> getAIFeedback(@RequestBody Map<String, Object> body) {
        String prompt = generatePrompt(body);

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> groqRequest = Map.of(
                "model", "llama3-70b-8192",
                "messages", List.of(
                        Map.of("role", "system", "content", """
                                    "You are an AI code reviewer integrated into a browser-based coding platform called CodeVerse. All code is executed via a stateless JDoodle compiler. You must strictly follow the platform’s automatic judging system:
                                
                                    - DO NOT interpret or guess intent based on descriptions. Only trust the actual test case comparison.
                                    - ✅ If the actual output matches the expected output exactly (including spacing, punctuation, and casing), return [RESULT]: PASS.
                                    - ❌ If the actual output does NOT match expected output, return [RESULT]: FAIL.
                                    - DO NOT suggest using `prompt()`, `Scanner`, `input()`, or `cin` unless input is explicitly required in the test case.
                                    - DO NOT assume or fabricate input/output variables like 'firstName', 'age', etc.
                                    - DO NOT suggest any changes unrelated to the actual failed test case.
                                
                                    Start your response strictly with one of:
                                    [RESULT]: PASS
                                    or
                                    [RESULT]: FAIL
                                
                                    Always follow these judging rules. Do not act like a conversational assistant — you are a strict output-based judge."
                                """),
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

        boolean hasTestCases = !(input.isBlank() && expected.isBlank() && actual.isBlank());
        boolean inputRequired = hasTestCases && !exerciseTasks.toLowerCase().contains("no input") && !exerciseDescription.toLowerCase().contains("no input");

        String testCaseSection = (input.isBlank() && expected.isBlank() && actual.isBlank())
                ? """
                🧪 This exercise does NOT include any test cases or standard input/output requirements.
                • DO NOT assume there is input.
                • DO NOT add any prompt(), Scanner, input(), or cin statements.
                • Evaluate code based ONLY on the task description and code logic.
                """
                : String.format("""
                        🧪 Test Case:
                        • Input: %s
                        • Expected Output: %s
                        • Actual Output: %s%s
                        """,
                input,
                expected,
                actual,
                (expected != null && !expected.isBlank() && (actual == null || actual.isBlank()))
                        ? "\n⚠️ The code produced no output. This is likely a mistake."
                        : ""
        );

        String reviewContext = inputRequired
                ? "🔍 The above test case FAILED in our judge. Help the learner fix the problem."
                : "🔍 This exercise does NOT require input. Do NOT recommend prompt(), Scanner, or input(). Focus on logic and output format.";

        return String.format("""
                        You are an AI assistant integrated into **CodeVerse**, an online platform where learners solve coding challenges directly in the browser.
                        
                        🧪 Our system uses **JDoodle**, a stateless cloud-based compiler that runs code with:
                        • Raw input (via standard input methods only)
                        • Raw output (compared exactly to expected output)
                        
                        ⚠️ IMPORTANT platform rules (apply to **all languages**):
                        • ✅ Learners MUST read input using the standard input mechanism of the language:
                          - Python → `input()`
                          - Java → `Scanner`
                          - C++ → `cin`
                          - JavaScript → `prompt()` or assume values are pre-defined
                        • ❌ DO NOT suggest interactive prompts like `print("Enter your name:")`, `cout << "Input:"`, etc.
                        • ❌ DO NOT add extra print statements or comments in the output; it must match expected output **exactly** (including newlines and spacing).
                        • ❌ DO NOT hardcode input values (e.g., `a = 8`, `if (a == 12)`) when input **is required**.
                        • ✅ If input is NOT required (per task), hardcoding is acceptable as long as output is correct.
                        
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
                           - ✅ If it uses correct logic and produces correct output → respond with:
                             [RESULT]: PASS
                           - ❌ If it produces wrong output or violates platform rules → respond with:
                             [RESULT]: FAIL
                        
                        2) After the [RESULT], clearly explain WHY the code passes or fails (be concise and beginner-friendly).
                        
                        3) If [RESULT]: FAIL, provide a corrected code snippet in a code block.
                        
                        🛑 Do NOT recommend any `prompt()`, `Scanner`, or `input()` if the task explicitly says input is not needed.
                        
                        Start your response with:
                        [RESULT]: PASS
                        or
                        [RESULT]: FAIL
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

    @PostMapping("/summary")
    public ResponseEntity<?> summarizeLesson(@RequestBody AISummaryRequest req) {
        try {
            String locale = (req.getLocale() == null || req.getLocale().isBlank()) ? "en" : req.getLocale();

            String transcript = "";
            if (req.getVideoUrl() != null && !req.getVideoUrl().isBlank()) {
                transcript = deepgramService.transcribeFromUrl(req.getVideoUrl(), locale);
            }

            System.out.println(req.getVideoUrl());
            System.out.println("++++++++++++++++");
            System.out.println(transcript);
            String theoryText = toPlainText(req.getContentHtml());

            String systemPrompt = """
                    You are a concise learning assistant. Summarize lessons into clean MARKDOWN for beginners.
                    Rules:
                    - Use English (unless locale says otherwise).
                    - Be concise but clear.
                    - Structure with these sections (only include sections that make sense):
                      # TL;DR
                      # Key Points
                      # What You Will Learn / Steps
                      # Glossary
                      # Quick Quiz
                      # Next Recommendations
                    - Bullet lists are preferred. Keep each bullet short.
                    - Do NOT include any JSON. Return plain Markdown text only.
                    """;

            String userPrompt = """
                    Summarize the lesson below.
                    
                    Title: %s
                    Locale: %s
                    
                    === THEORY (plain text) ===
                    %s
                    
                    === TRANSCRIPT (from video, if any) ===
                    %s
                    """.formatted(
                    safe(req.getTitle()),
                    locale,
                    safe(theoryText),
                    safe(transcript)
            );

            String markdown = groqService.chat(systemPrompt, userPrompt, 0.2);
            if (markdown == null || markdown.isBlank()) {
                markdown = "# TL;DR\nNo content available to summarize.";
            }

            // 4) Trả text đơn giản cho FE
            return ResponseEntity.ok(Map.of(
                    "lessonId", safe(req.getLessonId()),
                    "title", safe(req.getTitle()),
                    "summary", markdown
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Summarization failed: " + e.getMessage()));
        }
    }

    public static String toPlainText(String html) {
        if (html == null || html.isBlank()) return "";
        // Clean potentially unsafe tags, keep basic formatting
        String safe = Jsoup.clean(html, Safelist.relaxed());
        Document doc = Jsoup.parse(safe);

        // Prefer text() to keep spacing compact but readable
        String text = doc.text();
        // Normalize spaces/newlines a bit
        text = text.replaceAll("\\s+", " ").trim();
        return text;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}

package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.AISummaryRequest.AISummaryRequest;
import codeverse.com.web_be.dto.request.AiCourseSuggestRequest.AICourseModuleGenerateRequest;
import codeverse.com.web_be.dto.request.AiCourseSuggestRequest.AITestCaseGenerateRequest;
import codeverse.com.web_be.dto.request.AiCourseSuggestRequest.AITheoryGenerateRequest;
import codeverse.com.web_be.dto.request.AiCourseSuggestRequest.AiCourseSuggestRequest;
import codeverse.com.web_be.dto.response.CourseModuleResponse.CourseModuleResponse;
import codeverse.com.web_be.dto.response.LessonResponse.LessonResponse;
import codeverse.com.web_be.dto.response.SystemResponse.ApiResponse;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.CourseModule;
import codeverse.com.web_be.entity.Lesson;
import codeverse.com.web_be.entity.TestCase;
import codeverse.com.web_be.repository.TestCaseRepository;
import codeverse.com.web_be.service.AIService.DeepgramService;
import codeverse.com.web_be.service.AIService.GroqService;
import codeverse.com.web_be.service.CourseModuleService.ICourseModuleService;
import codeverse.com.web_be.service.CourseService.ICourseService;
import codeverse.com.web_be.service.FunctionHelper.FunctionHelper;
import codeverse.com.web_be.service.LessonService.ILessonService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {
    private final String GROQ_API_KEY1 = System.getenv("GROQ_API_KEY1");
    private final String GROQ_API_KEY2 = System.getenv("GROQ_API_KEY2");
    private final String GROQ_API_KEY3 = System.getenv("GROQ_API_KEY3");
    private final String GROQ_API_KEY4 = System.getenv("GROQ_API_KEY4");
    private final String modelName = "llama-3.3-70b-versatile";
    private final DeepgramService deepgramService;
    private final GroqService groqService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ICourseService courseService;
    private final ICourseModuleService courseModuleService;
    private final ILessonService lessonService;
    private final TestCaseRepository testCaseRepository;
    private final FunctionHelper functionHelper;
    private final Random random = new Random();

    @PostMapping("/feedback")
    public ResponseEntity<?> getAIFeedback(@RequestBody Map<String, Object> body) {
        String prompt = generatePrompt(body);

        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> groqRequest = Map.of(
                "model", modelName,
                "messages", List.of(
                        Map.of("role", "system", "content", """
                                    "You are an AI code reviewer integrated into a browser-based coding platform called CodeVerse. All code is executed via a stateless JDoodle compiler. You must strictly follow the platform’s automatic judging system:
                                
                                    - DO NOT interpret or guess intent based on descriptions. Only trust the actual test case comparison.
                                    - ✅ If the actual output matches the expected output exactly (including spacing, punctuation, and casing), return [RESULT]: PASS.
                                    - ❌ If the actual output does NOT match expected output, return [RESULT]: FAIL.
                                    - ❌ DO NOT suggest using `prompt()`, `Scanner`, `input()`, or `cin` unless input is explicitly required in the test case.
                                    - ❌ DO NOT assume or fabricate input/output variables like 'firstName', 'age', etc.
                                    - ❌ DO NOT suggest any changes unrelated to the actual failed test case.
                                
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
        headers.setBearerAuth(GROQ_API_KEY1);

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

            String theoryText = toPlainText(req.getContentHtml());

            String systemPrompt = """
                    You are a learning assistant. Summarize lessons into clean MARKDOWN for beginners.
                    Rules:
                    - Use English (unless locale says otherwise).
                    - Be concise but clear.
                    - Use sections only if they make sense:
                      # Key Points
                      # Next Recommendations
                    - Prefer bullet lists.
                    - Do NOT include JSON. Return plain Markdown text only.
                    """;

            String userPrompt = """
                    Summarize the lesson below.
                    
                    Title: %s
                    Locale: %s
                    
                    === THEORY ===
                    %s
                    
                    === TRANSCRIPT ===
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
        String safe = Jsoup.clean(html, Safelist.relaxed());
        Document doc = Jsoup.parse(safe);
        String text = doc.text();
        text = text.replaceAll("\\s+", " ").trim();
        return text;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    @PostMapping("/course/module-list")
    public ApiResponse<?> generateModuleList(@RequestBody AICourseModuleGenerateRequest req) {
        try {
            String prompt = buildModuleGeneratePrompt(req);
            String systemPrompt = """
                    You are an assistant that generates structured course outlines for programming courses. Return VALID JSON ONLY. No markdown, no prose.
                    STRICT REQUIREMENTS:
                        - Output MUST be ONLY a JSON array, nothing else.
                        - DO NOT wrap inside an object.
                        - DO NOT include fields like title, description, language, or level at the root.
                        - Generate EXACTLY %d modules. Do not generate fewer or more.
                        - DO NOT repeat the available modules and lessons.
                        - Each module MUST have EXACTLY %d lessons inside "subLessons".
                        - Lesson type rules:
                          * If a module has only 1 lesson → that lesson MUST be "CODE".
                          * If a module has more than 1 lesson → the last lesson MUST be "EXAM", and all previous lessons MUST be "CODE".
                        - For each subLesson:
                          * "title": clear and descriptive
                          * "lessonType": "CODE" or "EXAM" (must follow the rules above)
                          * "duration": positive integer no more than 30 (minutes)
                          * "expReward": positive integer no more than 50 (points)
                    
                        SCHEMA (strict shape):
                        [
                          {
                            "title": "string",
                            "subLessons": [
                              {
                                "title": "string",
                                "lessonType": "CODE" | "EXAM",
                                "duration": integer,
                                "expReward": integer
                              }
                            ]
                          }
                        ]
                    """.formatted(req.getModules(), req.getLessons());

            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            body.put("temperature", 0.3);
            body.put("response_format", Map.of("type", "json_object"));
            body.put("messages", List.of(
                    Map.of("role", "system", "content",
                            systemPrompt),
                    Map.of("role", "user", "content", prompt)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(GROQ_API_KEY1);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> groqResp = restTemplate.postForEntity(
                    "https://api.groq.com/openai/v1/chat/completions", entity, String.class);

            if (!groqResp.getStatusCode().is2xxSuccessful()) {
                return ApiResponse.builder()
                        .code(groqResp.getStatusCode().value())
                        .result(Map.of("message", "Groq error", "raw", groqResp.getBody()))
                        .build();
            }

            String content = extractAssistantContent(groqResp.getBody());
            String json = stripCodeFenceIfAny(content);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            if (root.isObject()) {
                ArrayNode wrapper = mapper.createArrayNode();
                wrapper.add(root);
                root = wrapper;
            }
            Object modules =
                    mapper.convertValue(root,
                            Object.class);

            return ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .result(Map.of("modules", modules))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .result(Map.of("message", "GenerateOutline failed", "error", e.getMessage()))
                    .build();
        }

    }

    private String buildModuleGeneratePrompt(AICourseModuleGenerateRequest req) {
        Course course = courseService.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<CourseModuleResponse> existingModules =
                courseModuleService.getCourseModuleListByCourseId(course.getId());
        StringBuilder existingBuilder = new StringBuilder();
        if (existingModules != null && !existingModules.isEmpty()) {
            existingBuilder.append("Existing modules and lessons (DO NOT repeat these):\n");
            for (CourseModuleResponse m : existingModules) {
                existingBuilder.append("- Module: ").append(m.getTitle()).append("\n");
                if (m.getLessons() != null) {
                    for (LessonResponse l : m.getLessons()) {
                        existingBuilder.append("   * Lesson: ").append(l.getTitle())
                                .append(" (type=").append(l.getLessonType()).append(")\n");
                    }
                }
            }
        } else {
            existingBuilder.append("No existing modules or lessons.\n");
        }
        return """
                Generate a JSON array for course outline.
                
                Course Info (for context only, DO NOT repeat in output):
                - Title: %s
                - Description: %s
                - Language: %s
                - Level: %s
                
                %s
                """.formatted(
                course.getTitle(),
                course.getDescription(),
                course.getLanguage(),
                course.getLevel(),
                existingBuilder.toString()
        );
    }

    @PostMapping("/lesson/theory-draft")
    public ApiResponse<?> generateTheory(@RequestBody AITheoryGenerateRequest req) {
        try {
            if (req.getLessonId() == null || req.getTheoryTitle() == null || req.getTheoryTitle().isBlank()) {
                return ApiResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .result(Map.of("message", "lessonId and theoryTitle are required"))
                        .build();
            }

            Lesson lesson = lessonService.findById(req.getLessonId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));

            if ("EXAM".equalsIgnoreCase(lesson.getLessonType().toString())) {
                return ApiResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .result(Map.of("message", "Theory content is not generated for EXAM lessons"))
                        .build();
            }

            Course course = lesson.getCourseModule().getCourse();
            CourseModule module = lesson.getCourseModule();

            final int targetWords = 450;

            String systemPrompt = """
                    You are a precise technical writing assistant for programming courses.
                    Return VALID HTML ONLY. No markdown, no JSON, no backticks.
                    The HTML should be an ARTICLE FRAGMENT (no <html>, <head>, <body>).
                    Allowed tags: <h1>, <h2>, <p>, <ul>, <ol>, <li>, <pre>, <code>, <b>, <em>.
                    Keep code blocks inside <pre><code>…</code></pre>. No inline styles, no scripts, no iframes, no images.
                    Aim for about %d words. Keep paragraphs short and scannable.
                    Include one or two practical code snippets in the course language when relevant (≤ ~30 lines each).
                    
                    IMPORTANT:
                        - DO NOT insert "\\n" characters for line breaks.
                        - Format line breaks using proper HTML tags (<p>, <br>, <li>, etc.).
                        - Output should be continuous HTML without escaped newline characters.
                    
                    CONTENT REQUIREMENTS:
                        - If user provided EXISTING THEORY CONTENT:
                            * The user may provide existing THEORY CONTENT, already in valid HTML format.
                            * Carefully READ and UNDERSTAND the provided HTML before generating.
                            * Do NOT repeat or restate it verbatim.
                            * Write new material that COMPLEMENTS and EXPANDS it.
                            * Maintain the same tone, technical level (%s), and teaching style.
                            * Fill in missing explanations, add clarifying examples, or introduce related concepts.
                            * Ensure smooth logical flow when read together with the provided content.
                            * Ensure your generated HTML flows naturally after (or within) the given HTML.
                        - If NO existing content is provided, generate from scratch.
                    
                    CONTENT REQUIREMENTS:
                    - Follow with a brief intro <p> explaining why this matters at the %s level and common pitfalls.
                    - Use <h2> sections to cover 2–4 core ideas or steps.
                    - Close with a short recap and 3–5 bullet points of key takeaways (<ul><li>…</li></ul>).
                    """.formatted(targetWords,
                    course.getLevel().toString(),
                    course.getLevel().toString());
            System.out.println(systemPrompt);

            String userPrompt = buildTheoryPrompt(course, module, lesson, req);

            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            body.put("temperature", 0.3);

            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(GROQ_API_KEY2);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> groqResp = restTemplate.postForEntity(
                    "https://api.groq.com/openai/v1/chat/completions", entity, String.class);

            if (!groqResp.getStatusCode().is2xxSuccessful()) {
                return ApiResponse.builder()
                        .code(groqResp.getStatusCode().value())
                        .result(Map.of("message", "Groq error", "raw", groqResp.getBody()))
                        .build();
            }

            String content = extractAssistantContent(groqResp.getBody());
            String html = stripCodeFenceIfAny(content).trim();

            if (!html.startsWith("<")) {
                html = "<p>" + escapeHtml(html) + "</p>";
            }

            return ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .result(Map.of("draft", html.replaceAll(">\\s+<", "><")
                            .trim()))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .result(Map.of("message", "GenerateTheory failed", "error", e.getMessage()))
                    .build();
        }
    }

    private String buildTheoryPrompt(Course course, CourseModule module, Lesson lesson, AITheoryGenerateRequest req) {
        String courseLang = (course.getLanguage() == null) ? "GENERAL" : course.getLanguage().toString();
        String level = (course.getLevel() == null) ? "BEGINNER" : course.getLevel().toString();
        String content = "";
        if (req.getTheoryContent() != null && !req.getTheoryContent().isEmpty()) {
            content = "- Available Theory content HTML: " + req.getTheoryContent();
        }

        return """
                Generate theory content (HTML fragment only) for a programming course.
                
                CONTEXT (for guidance, DO NOT echo these fields in the output):
                - Course Title: %s
                - Course Description: %s
                - Course Language: %s
                - Course Level: %s
                - Module Title: %s
                - Lesson Title: %s
                - Lesson Type: %s
                - Target Theory Title (use this as the main theme): %s
                %s
                
                OUTPUT:
                - Return ONLY the HTML fragment as specified. No markdown, no JSON, no backticks.
                """.formatted(
                nullToEmpty(course.getTitle()),
                nullToEmpty(course.getDescription()),
                courseLang,
                level,
                nullToEmpty(module.getTitle()),
                nullToEmpty(lesson.getTitle()),
                nullToEmpty(lesson.getLessonType().toString()),
                nullToEmpty(req.getTheoryTitle()),
                content
        );
    }

    @PostMapping("/lesson/test-case")
    public ApiResponse<?> generateTestCases(@RequestBody AITestCaseGenerateRequest req) {
        try {
            if (req.getLessonId() == null || req.getTestCases() <= 0) {
                return ApiResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .result(Map.of("message", "lessonId and testCases > 0 are required"))
                        .build();
            }

            Lesson lesson = lessonService.findById(req.getLessonId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));

            if (!"CODE".equalsIgnoreCase(lesson.getLessonType().toString())) {
                return ApiResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .result(Map.of("message", "Test cases are only generated for CODE lessons"))
                        .build();
            }

            Course course = lesson.getCourseModule().getCourse();
            CourseModule module = lesson.getCourseModule();

            String systemPrompt = """
                        You are an assistant that generates programming test cases.
                    
                        REQUIREMENTS:
                        - Return ONLY a JSON array. No explanations, no markdown, no backticks.
                        - Each element must follow this schema:
                          {
                            "input": [ "string", "string", ... ],
                            "expectedOutput": "string"
                          }
                        - "input" is always an array of strings (representing multiple inputs).
                        - DO NOT duplicate with the available test cases.
                        - DO NOT concatenate inputs into a single string. Keep them as array elements.
                        - "expectedOutput" must be a single string.
                        - Generate EXACTLY %d test cases.
                        - VERY IMPORTANT:
                            * If the exercise or exercise tasks involve **input**, the "input" array MUST contain values.
                            * If the exercise explicitly has **no input** (e.g., just printing "Hello World"), then use "input": [].
                        - Do NOT provide explanation or code, only valid JSON.
                        - Pay close attention to the instruction and tasks: if they mention reading or entering input, "input" must not be empty.
                        - Ensure variety: include normal, edge, and error-handling cases when relevant.
                    
                        EXAMPLES (do not copy content, copy only the shape):
                        [
                            {
                                "input": ["8", "2"],
                                "expectedOutput": "10 6 16 4"
                            },
                            {
                                "input": ["Hello"],
                                "expectedOutput": "Hello"
                            },
                            {
                                "input": [],
                                "expectedOutput": "Hello World"
                            }
                        ]
                    """.formatted(req.getTestCases());

            String userPrompt = buildTestCasePrompt(course, module, lesson, req);

            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            body.put("temperature", 0.3);

            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(GROQ_API_KEY2);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> groqResp = restTemplate.postForEntity(
                    "https://api.groq.com/openai/v1/chat/completions", entity, String.class);

            if (!groqResp.getStatusCode().is2xxSuccessful()) {
                return ApiResponse.builder()
                        .code(groqResp.getStatusCode().value())
                        .result(Map.of("message", "Groq error", "raw", groqResp.getBody()))
                        .build();
            }

            String content = extractAssistantContent(groqResp.getBody());
            String json = stripCodeFenceIfAny(content).trim();

            ObjectMapper mapper = new ObjectMapper();
            Object result = mapper.readValue(
                    json,
                    Object.class
            );

            return ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .result(Map.of("testCases", result))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .result(Map.of("message", "GenerateTestCases failed", "error", e.getMessage()))
                    .build();
        }
    }

    private String buildTestCasePrompt(Course course, CourseModule module, Lesson lesson, AITestCaseGenerateRequest req) {
        String courseLang = (course.getLanguage() == null) ? "GENERAL" : course.getLanguage().toString();
        String level = (course.getLevel() == null) ? "BEGINNER" : course.getLevel().toString();

        String exerciseTasks = lesson.getExercise() != null && !lesson.getExercise().getTasks().isEmpty()
                ? lesson.getExercise().getTasks().stream()
                .map(t -> "- " + t.getDescription())
                .collect(Collectors.joining("\n"))
                : "No explicit exercise tasks provided.";
        List<TestCase> testCases = testCaseRepository.findByExerciseId(lesson.getExercise().getId())
                .orElse(null);
        String availableTestCases = testCases != null ? testCases.stream()
                .map(t -> "- Input: " + functionHelper.parseInputStringToList(t.getInput()) + ". Output: " + t.getExpectedOutput())
                .collect(Collectors.joining("\n"))
                : "No available test cases provided.";

        return """
                Generate %d test cases for the following exercise.
                
                CONTEXT (DO NOT repeat these fields in output):
                - Course: %s (%s, %s level)
                - Module: %s
                - Lesson: %s
                - Theory HTML (if available): %s
                - Exercise Instruction: %s
                - Exercise Tasks:
                %s
                - Existing test cases: %s
                
                OUTPUT:
                - JSON array only, following schema:
                  [
                    {
                      "input": ["val1", "val2"],
                      "expectedOutput": "string"
                    }
                  ]
                """.formatted(
                req.getTestCases(),
                nullToEmpty(course.getTitle()), courseLang, level,
                nullToEmpty(module.getTitle()),
                nullToEmpty(lesson.getTitle()),
                lesson.getTheory() != null ? lesson.getTheory().getContent() : "None",
                lesson.getExercise() != null ? nullToEmpty(lesson.getExercise().getInstruction()) : "None",
                exerciseTasks,
                availableTestCases
        );
    }

    @PostMapping("/lesson/quiz-bank/{lessonId}")
    public ApiResponse<?> generateQuizBank(@PathVariable Long lessonId) {
        try {
            if (lessonId == null) {
                return ApiResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .result(Map.of("message", "lessonId is required"))
                        .build();
            }

            Lesson lesson = lessonService.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));

            if (!"EXAM".equalsIgnoreCase(lesson.getLessonType().toString())) {
                return ApiResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .result(Map.of("message", "Quiz bank is only generated for EXAM lessons"))
                        .build();
            }

            Course course = lesson.getCourseModule().getCourse();
            CourseModule module = lesson.getCourseModule();

            String systemPrompt = """
                    You are an assistant that generates multiple-choice quizzes.
                    
                    REQUIREMENTS:
                    - Return ONLY a JSON array. No explanations, no markdown, no backticks.
                    - Must generate EXACTLY 30 quiz objects with different questions.
                    - Schema:
                      {
                        "question": "string",
                        "quizType": "SINGLE" | "MULTIPLE",
                        "answers": [
                          { "answer": "string", "isCorrect": true|false }
                        ]
                      }
                    - Rules:
                      * SINGLE → exactly 1 correct answer.
                      * MULTIPLE → at least 2 correct answers.
                      * Each question must have 3–4 answer options.
                      * All questions/answers must align with provided course/module/theory.
                      * Vary question style: mix definitions, purposes, syntax, code-output prediction, error detection, conceptual comparisons, and practical applications.
                      * Do NOT create duplicates or near-duplicates.
                      * Ensure clarity, correctness, and variety across the 30 questions.
                      * Generate EXACTLY 30 quiz objects.
                      * If that is not possible, generate BETWEEN 15 and 30 quiz objects.
                      * Never generate fewer than 15 or more than 30.
                    
                      FORMATTING RULES:
                      - All JSON must be strictly valid and parseable by Jackson.
                      - Any double quotes inside string values (like code snippets or text) must be escaped as \\".
                      - Do not use backticks (`) for code blocks. If code is shown, wrap it directly as a string with escaped quotes.
                    """;

            String userPrompt = buildQuizPrompt(course, module, lesson);

            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            body.put("temperature", 0.3);

            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(List.of(
                    GROQ_API_KEY3,
                    GROQ_API_KEY4
            ).get(random.nextInt(2)));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> groqResp = restTemplate.postForEntity(
                    "https://api.groq.com/openai/v1/chat/completions", entity, String.class);

            if (!groqResp.getStatusCode().is2xxSuccessful()) {
                return ApiResponse.builder()
                        .code(groqResp.getStatusCode().value())
                        .result(Map.of("message", "Groq error", "raw", groqResp.getBody()))
                        .build();
            }

            String content = extractAssistantContent(groqResp.getBody());
            String json = stripCodeFenceIfAny(content).trim();

            ObjectMapper mapper = new ObjectMapper();
            Object quizzes = mapper.readValue(
                    json,
                    Object.class
            );

            return ApiResponse.builder()
                    .code(HttpStatus.OK.value())
                    .result(Map.of("quizBank", quizzes))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .result(Map.of("message", "GenerateQuizBank failed", "error", e.getMessage()))
                    .build();
        }
    }

    private String buildQuizPrompt(Course course, CourseModule module, Lesson examLesson) {
        String courseLang = (course.getLanguage() == null) ? "GENERAL" : course.getLanguage().toString();
        String level = (course.getLevel() == null) ? "BEGINNER" : course.getLevel().toString();

        List<Lesson> precedingLessons = module.getLessons().stream()
                .filter(l -> l.getOrderIndex() < examLesson.getOrderIndex())
                .filter(l -> "CODE".equalsIgnoreCase(l.getLessonType().toString()))
                .toList();

        StringBuilder theoryContent = new StringBuilder();
        if (!precedingLessons.isEmpty()) {
            precedingLessons.forEach(l -> {
                if (l.getTheory() != null) {
                    theoryContent.append("Lesson: ").append(l.getTitle()).append("\n");
                    theoryContent.append(l.getTheory().getContent()).append("\n\n");
                }
            });
        } else {
            theoryContent.append("No preceding lessons. Use course/module context only.\n");
        }

        return """
                Generate quiz questions based on the following context.
                
                CONTEXT (Do NOT repeat these fields in output):
                - Course: %s (%s, %s level)
                - Module: %s
                - Exam Lesson: %s
                - Theory Content (from preceding lessons if any):
                %s
                """.formatted(
                nullToEmpty(course.getTitle()), courseLang, level,
                nullToEmpty(module.getTitle()),
                nullToEmpty(examLesson.getTitle()),
                theoryContent.toString()
        );
    }

    @PostMapping("/course/suggest")
    public ResponseEntity<?> suggestCourse(@RequestBody AiCourseSuggestRequest req) {
        try {
            String prompt = buildPrompt(req);
            Map<String, Object> body = new HashMap<>();
            body.put("model", modelName);
            body.put("temperature", 0.3);
            body.put("response_format", Map.of("type", "json_object"));

            body.put("messages", List.of(
                    Map.of("role", "system",
                            "content", "You are an AI course designer. Return VALID JSON ONLY. No markdown, no prose."),
                    Map.of("role", "user", "content", prompt)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(GROQ_API_KEY2);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> groqResp = restTemplate.postForEntity(
                    "https://api.groq.com/openai/v1/chat/completions", entity, String.class);

            if (!groqResp.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(groqResp.getStatusCode())
                        .body(Map.of("message", "Groq error", "raw", groqResp.getBody()));
            }

            String content = extractAssistantContent(groqResp.getBody());
            String json = stripCodeFenceIfAny(content);

            ObjectMapper mapper = new ObjectMapper();
            Object outline = mapper.readValue(json, Object.class);
            System.out.println(outline);

            return ResponseEntity.ok(Map.of("outline", outline));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "SuggestCourse failed", "error", e.getMessage()));
        }
    }

    private String buildPrompt(AiCourseSuggestRequest req) {
        return String.format("""
                        Generate a JSON course outline.
                        
                        Title: %s
                        Description: %s
                        Language: %s
                        Level: %s
                        CategoryId: %d
                        Paid: %s, Price: %s
                        Modules: %d
                        Lessons per module: %d
                        Time per lesson (minutes): %d
                        Exercises included: %s
                        Quiz included: %s (style=%s, questions=%d, types=%s)
                        Points per lesson: %d
                        Points per quiz question: %d
                        
                        STRICT REQUIREMENTS:
                        - Return ONLY valid JSON. No explanations. No markdown fences.
                        - Follow the schema EXACTLY. Do not add extra properties.
                        - For quizzes:
                          * Each question MUST include "quizType": "SINGLE" or "MULTIPLE".
                          * Use "SINGLE" if EXACTLY ONE answer has isCorrect=true.
                          * Use "MULTIPLE" if TWO OR MORE answers have isCorrect=true.
                          * Each question MUST have at least ONE correct answer.
                          * Answers: keep 3–6 options, clear and unambiguous.
                        
                        SCHEMA (example shape; populate with real content):
                        {
                          "suggestedTitle": "string",
                          "suggestedDescription": "string",
                          "modules": [
                            {
                              "title": "string",
                              "lessons": [
                                {
                                  "title": "string",
                                  "type": "CODE" | "EXAM",
                                  "duration": %d,
                                  "points": %d,
                                  "objective": "string",
                                  "theory": {
                                    "title": "string",
                                    "contentHtml": "<p>HTML allowed</p>"
                                  },
                                  "exercise": {
                                    "title": "string",
                                    "instruction": "string",
                                    "tasks": ["string"],
                                    "testCases": [
                                      {
                                        "input": ["line1", "line2"],   // stdin lines as array of strings
                                        "expected": "string",          // exact expected stdout
                                        "priority": "REQUIRED" | "OPTIONAL",
                                        "public": true
                                      }
                                    ]
                                  },
                                  "quiz": {
                                    "questions": [
                                      {
                                        "quizType": "SINGLE" | "MULTIPLE",
                                        "question": "string",
                                        "points": %d,
                                        "answers": [
                                          { "answer": "string", "isCorrect": true },
                                          { "answer": "string", "isCorrect": false }
                                        ]
                                      }
                                    ]
                                  }
                                }
                              ]
                            }
                          ]
                        }
                        """,
                // ==== data binding ====
                req.base.courseTitle,
                req.base.courseDescription,
                req.base.language,
                req.base.levelId,
                req.base.categoryId,
                req.base.isPaid,
                req.base.price,
                req.structure.moduleCount,
                req.structure.lessonsPerModule,
                req.structure.timePerLesson,
                req.exercises.include,
                req.quiz.include,
                req.quiz.style,
                req.quiz.questionsPerQuiz,
                req.quiz.types,
                req.scoring.pointsPerLesson,
                req.scoring.pointsPerQuizQuestion,
                req.structure.timePerLesson,
                req.scoring.pointsPerLesson,
                req.scoring.pointsPerQuizQuestion
        );
    }

    private String extractAssistantContent(String raw) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(raw);
        return node.path("choices").get(0).path("message").path("content").asText();
    }

    private String stripCodeFenceIfAny(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.startsWith("```")) {
            // remove leading ```
            t = t.substring(3).trim();
            // remove optional 'json'
            if (t.toLowerCase().startsWith("json")) {
                t = t.substring(4).trim();
            }
            // cut at ending ```
            int i = t.lastIndexOf("```");
            if (i >= 0) t = t.substring(0, i).trim();
        }
        return t;
    }

    private String nullToEmpty(String s) {
        return (s == null) ? "" : s;
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @PostMapping("/quiz-feedback")
    public ResponseEntity<?> getAIQuizFeedback(@RequestBody Map<String, Object> body) {
        try {
            String quizTitle = (String) body.getOrDefault("quizTitle", "Quiz");
            List<Map<String, Object>> wrongAnswers = (List<Map<String, Object>>) body.get("wrongAnswers");

            String systemPrompt = """
                    You are a strict AI tutor helping learners understand why their quiz answers are wrong.
                    
                    REQUIREMENTS:
                    - Return ONLY valid JSON array. No markdown, no prose, no backticks.
                    - Each element must have exactly this schema:
                      {
                        "question": "string",
                        "userAnswer": ["string", ...],
                        "correctAnswers": ["string", ...],
                        "explanation": "string"
                      }
                    - Explanation must be concise, beginner-friendly, and correct.
                    - Do NOT merge multiple questions into one explanation. Each question must have its own object.
                    """;

            String userPrompt = "Quiz Title: " + quizTitle + "\n\nWrong answers:\n" +
                    wrongAnswers.stream()
                            .map(w -> "- Question: " + w.get("question") +
                                    "\n  User Answer: " + w.get("userAnswer") +
                                    "\n  Correct Answers: " + w.get("correctAnswers"))
                            .collect(Collectors.joining("\n\n"));

            Map<String, Object> bodyReq = Map.of(
                    "model", modelName,
                    "temperature", 0.3,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(GROQ_API_KEY3);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(bodyReq, headers);
            ResponseEntity<String> groqResp = restTemplate.postForEntity(
                    "https://api.groq.com/openai/v1/chat/completions",
                    entity,
                    String.class
            );

            if (!groqResp.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(groqResp.getStatusCode())
                        .body(Map.of("message", "Groq error", "raw", groqResp.getBody()));
            }

            String content = extractAssistantContent(groqResp.getBody());
            String json = stripCodeFenceIfAny(content).trim();

            ObjectMapper mapper = new ObjectMapper();
            Object result = mapper.readValue(json, Object.class);

            return ResponseEntity.ok(Map.of("feedback", result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "QuizFeedback failed", "error", e.getMessage()));
        }
    }
}

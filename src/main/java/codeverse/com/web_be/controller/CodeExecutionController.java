package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.CodeExecutionRequest.CodeExecutionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/code")
@RequiredArgsConstructor
public class CodeExecutionController {
    @PostMapping("/execute")
    public ResponseEntity<?> executeCode(@RequestBody CodeExecutionRequest request) {
        try {
            String result;
            switch (request.getLanguage()) {
                case "javascript":
                    result = runJavascript(request.getCode(), request.getInput());
                    break;
                case "java":
                    result = runJava(request.getCode(), request.getInput());
                    break;
                default:
                    return ResponseEntity.badRequest().body(Map.of("error", "Unsupported language"));
            }

            return ResponseEntity.ok(Map.of("output", result));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    private String runJava(String code, String input) throws IOException, InterruptedException {
        if (code == null || code.isEmpty()) {
            throw new RuntimeException("Java code is empty.");
        }

        boolean hasInput = (input != null && !input.trim().isEmpty());
        if (hasInput) {
            input = input.replace(",", " ");
        }

        String className = code.split("class ")[1].split(" ")[0].trim();
        Path tempDir = Files.createTempDirectory("java-code");
        Path javaFile = tempDir.resolve(className + ".java");
        String modifiedCode = "import java.io.File;\nimport java.io.FileNotFoundException;\n" +
                code.replace("Scanner sc = new Scanner(System.in);",
                        "Scanner sc = new Scanner(" + (hasInput ? "new File(\"input.txt\")" : "System.in") + ");");
        if (!modifiedCode.contains("throws FileNotFoundException")) {
            modifiedCode = modifiedCode.replace("public static void main(String[] args) {",
                    "public static void main(String[] args) throws FileNotFoundException {");
        }

        Files.writeString(javaFile, modifiedCode);

        if (hasInput) {
            Path inputFile = tempDir.resolve("input.txt");
            Files.writeString(inputFile, input);
        }

        Process compile = new ProcessBuilder("javac", javaFile.toString())
                .directory(tempDir.toFile())
                .redirectErrorStream(true)
                .start();
        String compileOutput = new String(compile.getInputStream().readAllBytes());
        int compileCode = compile.waitFor();

        if (compileCode != 0) {
            throw new RuntimeException("Compile error:\n" + compileOutput);
        }

        ProcessBuilder runBuilder = new ProcessBuilder("java", "-cp", tempDir.toString(), className)
                .directory(tempDir.toFile())
                .redirectErrorStream(true);

        if (hasInput) {
            Path inputFile = tempDir.resolve("input.txt");
            runBuilder.redirectInput(inputFile.toFile());
        }

        Process run = runBuilder.start();
        String output = new String(run.getInputStream().readAllBytes());
        int runCode = run.waitFor();
        Files.deleteIfExists(javaFile);

        if (hasInput) {
            Files.deleteIfExists(tempDir.resolve("input.txt"));
        }

        Files.deleteIfExists(tempDir.resolve(className + ".class"));
        Files.deleteIfExists(tempDir);

        if (runCode != 0) {
            throw new RuntimeException("Runtime error:\n" + output);
        }

        return output.trim();
    }




    private String runJavascript(String code, String input) throws IOException, InterruptedException {
        Path file = Files.createTempFile("usercode-", ".js");
        String fullCode = code + "\nconsole.log(run(" + input + "));";
        Files.writeString(file, fullCode);

        ProcessBuilder pb = new ProcessBuilder("node", file.toAbsolutePath().toString());
        pb.redirectErrorStream(true);
        Process process = pb.start();

        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();

        Files.delete(file);

        if (exitCode != 0) {
            throw new RuntimeException("Code exited with " + exitCode);
        }

        return output.trim();
    }
}

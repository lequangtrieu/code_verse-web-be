package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.request.CodeExecutionRequest.CodeExecutionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/code")
@RequiredArgsConstructor
public class CodeExecutionController {

    @PostMapping("/execute")
    public ResponseEntity<?> executeJavaInDocker(@RequestBody CodeExecutionRequest request) {
        if (request.getCode() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing code"));
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "docker", "run", "--rm",
                    "-e", "CODE=" + request.getCode().replace("\"", "\\\""),
                    "-e", "INPUT=" + (request.getInput() == null ? "" : request.getInput().replace("\"", "\\\"")),
                    "java-runner"
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();
            String output = new BufferedReader(new InputStreamReader(process.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
            int exitCode = process.waitFor();

            return ResponseEntity.ok(Map.of("output", output, "exitCode", exitCode));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

//    @PostMapping("/execute")
//    public ResponseEntity<?> executeCode(@RequestBody CodeExecutionRequest request) {
//        try {
//            if (request.getLanguage() == null || request.getCode() == null) {
//                return ResponseEntity.badRequest().body(Map.of(
//                        "error", "Missing language or code"
//                ));
//            }
//
//            String language = convertLanguageName(request.getLanguage());
//            String versionIndex = getVersionIndex(language);
//
//            List<Map<String, String>> credentialsList = List.of(
//                    Map.of("clientId", "bbb818b20e36927c1e4987bbe30bcaa1", "clientSecret", "9c457e85f69ee0226d3959dd9aa9e1eb21fff57ffe7d427196caf104022627ce"),
//                    Map.of("clientId", "ae2b51a5522d0f58cd09cddebb4c58a6", "clientSecret", "593e01831cdd9673d0a8ab02bbc35c8130e27add74c84afffc5412309b9456bc"),
//                    Map.of("clientId", "c0afa9758fd7e14891882a980bcb2877", "clientSecret", "36a60b7977814f407511c6dbebc5e9267d0d60cde49b7c3ea9c435e2b6e77f69"),
//                    Map.of("clientId", "7d4e8a8232f4dc97640933cc0389576b", "clientSecret", "5bd9e9108fc745d9d3f22ab6399ad52dbe18edaee39bbb1faef31bf4ea6b99ba"),
//                    Map.of("clientId", "61b9fb6140c7508ffc31e39d05c4e9c6", "clientSecret", "87b755741b2c0ffabf35e9940d3f2b50270d79d6a59322f8fa82cdd755620ecb"),
//                    Map.of("clientId", "b8205ff563bfeb867ee288ef19a7286a", "clientSecret", "96390389f83edc37ad1edb7ceb720e1f62617a1b39ac056c27e23a033787de7f"),
//                    Map.of("clientId", "c256b74302700463c1ca4d1a4cc07f62", "clientSecret", "3b05ad8651782602d3a27190b76bdc0b4674c4459a229ed6f3e095aec794205e"),
//                    Map.of("clientId", "e1cd030eec006dcbecf4ef66bccb5bc9", "clientSecret", "ea5891b2bbfe504fe5c6e0d096eeb5ef949ed158c0ccad4036db9d3a94e9fd0"),
//                    Map.of("clientId", "8fe702687c9640df445647ae66ea165b", "clientSecret", "dfd578c367432b4654df1ccca24214d48a8bd808ea223c45af92f9cebbe512a7"),
//                    Map.of("clientId", "17cae29a485c6f8d4cd8ec8b23c1798d", "clientSecret", "fec9dbfcb68a4bf36aa05caa01188ccfd5fa81652ad1fe6cca0d01685b9fde34"),
//                    Map.of("clientId", "7cc607cedddb0ac80e1f5c29ba578adc", "clientSecret", "fda43bd8cec73856bc18a0fa011823f21f5bb67a06e1f6f7c2b0dfe85d8aee12"),
//                    Map.of("clientId", "73c9be18b31bf13564a7d18230b3f09", "clientSecret", "8b08c342573c726f8e130eb1f0bada98b4c43082824a139e0adabe8887676340")
//                    );
//
//            RestTemplate restTemplate = new RestTemplate();
//
//
//            for (Map<String, String> creds : credentialsList) {
//                Map<String, Object> jdoodleRequest = Map.of(
//                        "clientId", creds.get("clientId"),
//                        "clientSecret", creds.get("clientSecret"),
//                        "script", request.getCode(),
//                        "stdin", request.getInput() == null ? "" : request.getInput(),
//                        "language", language,
//                        "versionIndex", versionIndex
//                );
//
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
//                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(jdoodleRequest, headers);
//
//                try {
//                    ResponseEntity<Map> jdoodleResponse = restTemplate.postForEntity(
//                            "https://api.jdoodle.com/v1/execute", requestEntity, Map.class
//                    );
//
//                    Object output = jdoodleResponse.getBody().get("output");
//                    return ResponseEntity.ok(Map.of(
//                            "output", output,
//                            "status", "JDoodle executed successfully with fallback index: " + credentialsList.indexOf(creds)
//                    ));
//
//                } catch (HttpClientErrorException | HttpServerErrorException ex) {
//                    String errJson = ex.getResponseBodyAsString();
//                    try {
//                        ObjectMapper mapper = new ObjectMapper();
//                        Map<String, Object> errorMap = mapper.readValue(errJson, Map.class);
//                        String errorMessage = errorMap.getOrDefault("error", "").toString().toLowerCase();
//                        if (errorMessage.contains("quota") || errorMessage.contains("limit")) {
//                        } else {
//                            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", errorMap));
//                        }
//
//                    } catch (Exception parseEx) {
//                        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", errJson));
//                    }
//                }
//            }
//
//            return ResponseEntity.status(429).body(Map.of(
//                    "error", "All JDoodle credentials exhausted or failed"
//            ));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body(Map.of(
//                    "error", "Unexpected error: " + e.getMessage()
//            ));
//        }
//    }

    private String convertLanguageName(String inputLang) {
        return switch (inputLang.toLowerCase()) {
            case "javascript" -> "nodejs";
            case "cpp" -> "cpp17";
            case "c" -> "c";
            case "java" -> "java";
            case "python" -> "python3";
            default -> inputLang.toLowerCase();
        };
    }

    private String getVersionIndex(String language) {
        return switch (language) {
            case "java" -> "3";
            case "nodejs" -> "3";
            case "python3" -> "3";
            case "cpp17" -> "0";
            case "c" -> "5";
            default -> "0";
        };
    }
}

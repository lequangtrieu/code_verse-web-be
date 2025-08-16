package codeverse.com.web_be.service.AIService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeepgramService {

    private String deepgramApiKey = System.getenv("DEEPGRAM_API_KEY");

    @Value("${ai.deepgram.model:nova-2}")
    private String deepgramModel;

    private static final String DG_URL = "https://api.deepgram.com/v1/listen";

    public String transcribeFromUrl(String mediaUrl, String language) {
        if (mediaUrl == null || mediaUrl.isBlank()) return "";

        RestTemplate rt = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(DG_URL)
                .queryParam("model", deepgramModel)
                .queryParam("smart_format", "true")
                .queryParam("punctuate", "true")
                .queryParam("paragraphs", "true")
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(deepgramApiKey);

        Map<String, Object> body = Map.of("url", mediaUrl);
        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> res = rt.postForEntity(url, req, Map.class);
            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) return "";

            Map results = (Map) res.getBody().get("results");
            if (results == null) return "";

            List<Map> channels = (List<Map>) results.get("channels");
            if (channels == null || channels.isEmpty()) return "";

            Map firstChannel = channels.get(0);
            List<Map> alts = (List<Map>) firstChannel.get("alternatives");
            if (alts == null || alts.isEmpty()) return "";

            Map alt0 = alts.get(0);
            String transcript = (String) alt0.getOrDefault("transcript", "");

            Map paragraphs = (Map) alt0.get("paragraphs");
            if (paragraphs != null) {
                List<Map> paras = (List<Map>) paragraphs.get("paragraphs");
                if (paras != null && !paras.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Map p : paras) {
                        String t = (String) p.getOrDefault("text", "");
                        if (t != null && !t.isBlank()) sb.append(t.trim()).append("\n");
                    }
                    String joined = sb.toString().trim();
                    if (!joined.isBlank()) transcript = joined;
                }
            }

            return transcript == null ? "" : transcript.trim();
        } catch (Exception e) {
            return "";
        }
    }
}
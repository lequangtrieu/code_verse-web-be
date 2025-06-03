package codeverse.com.web_be.service.FirebaseService;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlMediaExtractor {
    private static final Pattern IMG_SRC_PATTERN = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);
    private static final Pattern VIDEO_SRC_PATTERN = Pattern.compile("<video[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE);

    public static Set<String> extractMediaUrlsFromHtml(String html) {
        Set<String> urls = new HashSet<>();

        if (html == null || html.isEmpty()) return urls;

        Matcher imgMatcher = IMG_SRC_PATTERN.matcher(html);
        while (imgMatcher.find()) {
            urls.add(imgMatcher.group(1));
        }

        Matcher videoMatcher = VIDEO_SRC_PATTERN.matcher(html);
        while (videoMatcher.find()) {
            urls.add(videoMatcher.group(1));
        }

        return urls;
    }
}

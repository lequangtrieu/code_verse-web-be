package codeverse.com.web_be.dto.common;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CodeVerseUpdateModel {
    private final LocalDateTime updatedAt = LocalDateTime.now();
}

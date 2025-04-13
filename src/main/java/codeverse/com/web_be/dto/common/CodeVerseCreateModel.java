package codeverse.com.web_be.dto.common;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class CodeVerseCreateModel {
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now();
}

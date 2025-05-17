package codeverse.com.web_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_progress_id", nullable = false)
    private LessonProgress lessonProgress;

    @Column(columnDefinition = "TEXT")
    private String code;

    @Column(name = "execution_time")
    private Float executionTime;

    @Column(name = "memory_usage")
    private Float memoryUsage;
}
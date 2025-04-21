package codeverse.com.web_be.entity;

import codeverse.com.web_be.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submission", indexes = {
    @Index(name = "idx_submission_exercise", columnList = "exercise_id"),
    @Index(name = "idx_submission_learner", columnList = "learner_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "learner_id", nullable = false)
    private User learner;

    @Column(columnDefinition = "TEXT")
    private String code;

    @Column(name = "execution_time")
    private Float executionTime;

    @Column(name = "memory_usage")
    private Float memoryUsage;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    @Column(name = "pass_rate")
    private Float passRate;

    @Column(name = "test_case_count")
    private Integer testCaseCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
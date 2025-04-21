package codeverse.com.web_be.entity;

import codeverse.com.web_be.enums.TestCasePriority;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_case")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(columnDefinition = "TEXT")
    private String input;

    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;

    @Enumerated(EnumType.STRING)
    private TestCasePriority priority;

    @Column(name = "is_public", columnDefinition = "tinyint(1) default 0")
    @Builder.Default
    private boolean isPublic = false;
}
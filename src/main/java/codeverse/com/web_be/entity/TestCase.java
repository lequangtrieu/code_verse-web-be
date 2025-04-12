package codeverse.com.web_be.entity;

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
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @Column(columnDefinition = "TEXT")
    private String input;

    @Column(columnDefinition = "TEXT")
    private String expectedOutput;
}
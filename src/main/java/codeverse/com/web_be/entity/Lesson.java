package codeverse.com.web_be.entity;

import codeverse.com.web_be.enums.LessonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "lesson")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id ", nullable = false)
    private CourseModule courseModule;

    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY)
    private List<QuizQuestion> quizQuestions;

    @Column(nullable = false)
    private String title;

    @Column(name = "order_index")
    private Integer orderIndex;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Theory theory;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Exercise exercise;

    @Column(name = "duration", columnDefinition = "integer default 10")
    private Integer duration;

    @Enumerated(EnumType.STRING)
    private LessonType lessonType;

    @Column(name = "exp_reward")
    private Integer expReward;

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
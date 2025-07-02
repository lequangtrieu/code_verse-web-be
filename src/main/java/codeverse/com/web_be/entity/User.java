package codeverse.com.web_be.entity;

import codeverse.com.web_be.enums.InstructorStatus;
import codeverse.com.web_be.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String name;

    @Column(nullable = false)
    private String password;

    private String avatar;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "is_deleted", columnDefinition = "tinyint(1) default 0")
    @Builder.Default
    private Boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    private InstructorStatus instructorStatus;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "teaching_credentials", columnDefinition = "TEXT")
    private String teachingCredentials;

    @Column(name = "educational_background", columnDefinition = "TEXT")
    private String educationalBackground;

    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user", "hibernateLazyInitializer", "handler"})
    private List<Cart> carts;

    public User(Long userId) {
        this.id = userId;
    }

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
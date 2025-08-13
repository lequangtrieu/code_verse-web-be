package codeverse.com.web_be.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "report_reason")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Boolean active = true;
}

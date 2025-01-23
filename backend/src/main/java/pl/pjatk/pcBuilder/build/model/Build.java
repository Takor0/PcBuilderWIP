package pl.pjatk.pcBuilder.build.model;

import jakarta.persistence.*;
import lombok.*;
import pl.pjatk.pcBuilder.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Build")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Build {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String buildConfiguration;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
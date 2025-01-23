package pl.pjatk.pcBuilder.build.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import pl.pjatk.pcBuilder.user.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Build")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Build {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String buildConfiguration;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
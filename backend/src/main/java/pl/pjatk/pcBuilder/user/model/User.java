package pl.pjatk.pcBuilder.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "isVerified")
    private Boolean isVerified = false;

    @Column(name = "isAdmin")
    private Boolean isAdmin = false;

    @Column(name = "failedLoginAttempts")
    private Integer failedLoginAttempts = 0;

    @Column(name = "accountLockedUntil")
    private LocalDateTime accountLockedUntil;
}
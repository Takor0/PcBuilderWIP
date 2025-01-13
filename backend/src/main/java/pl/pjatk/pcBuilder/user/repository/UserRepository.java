package pl.pjatk.pcBuilder.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.pjatk.pcBuilder.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 
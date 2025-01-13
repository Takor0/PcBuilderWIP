package pl.pjatk.pcBuilder.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pl.pjatk.pcBuilder.user.model.User;
import pl.pjatk.pcBuilder.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        logger.info("Próba rejestracji użytkownika: {}", user.getUsername());

        User existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser != null) {
            logger.warn("Użytkownik o nazwie {} już istnieje w bazie (ID: {})", user.getUsername(), existingUser.getId());
            throw new RuntimeException("Użytkownik o nazwie " + user.getUsername() + " już istnieje w bazie (ID: " + existingUser.getId() + ")");
        }

        User existingEmail = userRepository.findByEmail(user.getEmail());
        if (existingEmail != null) {
            logger.warn("Email {} jest już zajęty przez użytkownika {}", user.getEmail(), existingEmail.getUsername());
            throw new RuntimeException("Email " + user.getEmail() + " jest już zajęty przez użytkownika " + existingEmail.getUsername());
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        user.setIsVerified(false);
        User savedUser = userRepository.save(user);
        logger.info("Użytkownik {} został pomyślnie zarejestrowany", user.getUsername());
        return savedUser;
    }

    public User findByUsername(String username) {
        logger.info("Wyszukiwanie użytkownika: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("Nie znaleziono użytkownika: {}", username);
            throw new RuntimeException("Nie znaleziono użytkownika o nazwie: " + username);
        }
        return user;
    }

    public User verifyUser(Long userId) {
        logger.info("Próba weryfikacji użytkownika o ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Nie znaleziono użytkownika o ID: {}", userId);
                    return new RuntimeException("Nie znaleziono użytkownika o ID: " + userId);
                });
        user.setIsVerified(true);
        User verifiedUser = userRepository.save(user);
        logger.info("Użytkownik o ID {} został zweryfikowany", userId);
        return verifiedUser;
    }

    public User loginUser(String usernameOrEmail, String rawPassword) {
        logger.info("Próba logowania użytkownika: {}", usernameOrEmail);
        
        User user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }

        if (user == null) {
            logger.warn("Nieprawidłowe dane logowania dla: {}", usernameOrEmail);
            throw new RuntimeException("Nieprawidłowe dane logowania");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            logger.warn("Nieprawidłowe hasło dla użytkownika: {}", usernameOrEmail);
            throw new RuntimeException("Nieprawidłowe dane logowania");
        }

        if (!user.getIsVerified()) {
            logger.warn("Użytkownik {} nie został zweryfikowany", usernameOrEmail);
            throw new RuntimeException("Użytkownik nie został jeszcze zweryfikowany.");
        }

        logger.info("Użytkownik {} zalogował się pomyślnie", usernameOrEmail);
        return user;
    }

}

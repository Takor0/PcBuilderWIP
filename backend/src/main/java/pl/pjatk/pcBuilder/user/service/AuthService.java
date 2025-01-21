package pl.pjatk.pcBuilder.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pl.pjatk.pcBuilder.config.JwtUtil;
import pl.pjatk.pcBuilder.user.model.User;
import pl.pjatk.pcBuilder.user.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Set<String> invalidatedTokens = new HashSet<>();

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

    public String loginWithToken(String usernameOrEmail, String rawPassword) {
        logger.info("Próba logowania użytkownika: {}", usernameOrEmail);

        User user = userRepository.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepository.findByEmail(usernameOrEmail);
        }

        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            logger.warn("Nieprawidłowe dane logowania dla: {}", usernameOrEmail);
            throw new RuntimeException("Nieprawidłowe dane logowania");
        }

        if (!user.getIsVerified()) {
            logger.warn("Użytkownik {} nie został zweryfikowany", usernameOrEmail);
            throw new RuntimeException("Użytkownik nie został jeszcze zweryfikowany.");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        logger.info("Token wygenerowany dla użytkownika: {}", usernameOrEmail);
        return token;
    }

    public String validateToken(String token) {
        if (invalidatedTokens.contains(token)) {
            throw new RuntimeException("Token został unieważniony");
        }
        return jwtUtil.validateToken(token);
    }

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
        logger.info("Token został unieważniony");
    }

}

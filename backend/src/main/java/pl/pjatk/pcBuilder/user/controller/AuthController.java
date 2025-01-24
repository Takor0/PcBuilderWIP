package pl.pjatk.pcBuilder.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import pl.pjatk.pcBuilder.user.dto.LoginRequest;
import pl.pjatk.pcBuilder.user.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            String token = authService.loginWithToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());

            Cookie cookie = new Cookie("auth-token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600);
            response.addCookie(cookie);

            return ResponseEntity.ok("Zalogowano pomyślnie");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                    @CookieValue(value = "auth-token", required = false) String cookieToken,
                                    HttpServletResponse response) {
        try {
            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.replace("Bearer ", "");
            } else if (cookieToken != null) {
                token = cookieToken;
            }

            if (token == null) {
                throw new RuntimeException("Nie znaleziono tokena do wylogowania");
            }

            authService.invalidateToken(token);

            Cookie cookie = new Cookie("auth-token", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            
            return ResponseEntity.ok("Wylogowano pomyślnie");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Błąd podczas wylogowywania: " + e.getMessage());
        }
    }


    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                      @CookieValue(value = "auth-token", required = false) String cookieToken) {
        try {
            String token;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.replace("Bearer ", "");
            } else if (cookieToken != null) {
                token = cookieToken;
            } else {
                throw new RuntimeException("Token nie został dostarczony");
            }

            String username = authService.validateToken(token);
            return ResponseEntity.ok("Token jest ważny dla użytkownika: " + username);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }


}

package pl.pjatk.pcBuilder.build.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.pcBuilder.build.model.BuildConfiguration;
import pl.pjatk.pcBuilder.build.model.BuildRequest;
import pl.pjatk.pcBuilder.build.service.BuildService;
import pl.pjatk.pcBuilder.user.model.User;
import pl.pjatk.pcBuilder.user.service.AuthService;

@RestController
@RequestMapping("/api/builds")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BuildController {
    private final BuildService buildService;
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    @PostMapping("/generate")
    public ResponseEntity<?> generateBuild(
            @RequestBody BuildRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
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
                logger.error("Nie znaleziono tokena w żądaniu");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Użytkownik nie jest uwierzytelniony"));
            }

            String username = authService.validateToken(token);
            if (username == null) {
                logger.error("Token jest nieprawidłowy lub wygasł");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Token jest nieprawidłowy lub wygasł"));
            }

            User user = authService.findByUsername(username);
            if (user == null) {
                logger.error("Użytkownik {} nie istnieje", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Użytkownik nie istnieje"));
            }

            if (!user.getIsVerified()) {
                logger.warn("Użytkownik {} nie został zweryfikowany", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Użytkownik nie został jeszcze zweryfikowany."));
            }

            if (request.getBudget() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Budżet musi być większy od 0"));
            }

            BuildConfiguration config = buildService.generateBuildConfiguration(request);
            buildService.saveBuild(config, user);

            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("Błąd podczas generowania builda: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Wystąpił błąd podczas generowania builda"));
        }
    }

    private static class ErrorResponse {
        private final String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}

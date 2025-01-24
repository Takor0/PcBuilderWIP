package pl.pjatk.pcBuilder.build.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.pcBuilder.build.model.BuildConfiguration;
import pl.pjatk.pcBuilder.build.model.BuildRequest;
import pl.pjatk.pcBuilder.build.service.BuildService;
import pl.pjatk.pcBuilder.user.model.User;
import pl.pjatk.pcBuilder.user.service.AuthService;
import pl.pjatk.pcBuilder.build.model.Build;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/builds")
@RequiredArgsConstructor
public class BuildController {
    private final BuildService buildService;
    private final AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    private String formatTotalPrice(double totalPrice) {
        DecimalFormat df = new DecimalFormat("#.00", new DecimalFormatSymbols(Locale.US));
        return df.format(totalPrice);
    }

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

            double totalPrice = config.getTotalPrice();
            String formattedTotalPrice = formatTotalPrice(totalPrice);


            Map<String, Object> responseMap = new LinkedHashMap<>();
            responseMap.put("cpu", config.getCpu());
            responseMap.put("gpu", config.getGpu());
            responseMap.put("motherboard", config.getMotherboard());
            responseMap.put("memory", config.getMemory());
            responseMap.put("storage", config.getStorage());
            responseMap.put("pcCase", config.getPcCase());
            responseMap.put("powerSupply", config.getPowerSupply());
            responseMap.put("totalPrice", Double.parseDouble(formattedTotalPrice));
            responseMap.put("performanceScores", config.getPerformanceScores());
            responseMap.put("componentDescriptions", config.getComponentDescriptions());

            return ResponseEntity.ok(responseMap);

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

    @GetMapping("/get")
    public ResponseEntity<?> getUserBuilds(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @CookieValue(value = "auth-token", required = false) String cookieToken) {

        try {
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.replace("Bearer ", "");
            } else if (cookieToken != null) {
                token = cookieToken;
            }
            if (token == null) {
                logger.error("No token provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Authentication required"));
            }
            String username = authService.validateToken(token);
            if (username == null) {
                logger.error("Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid or expired token"));
            }
            User user = authService.findByUsername(username);
            if (user == null) {
                logger.error("User not found for username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("User not found"));
            }
            if (!user.getIsVerified()) {
                logger.warn("User {} is not verified", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("User not verified"));
            }

            List<Build> config = buildService.getBuildsByUsername(username);

            return ResponseEntity.ok(config);

        } catch (Exception e) {
            logger.error("Error fetching builds for user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving builds"));
        }
    }

}

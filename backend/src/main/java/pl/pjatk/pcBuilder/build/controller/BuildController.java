package pl.pjatk.pcBuilder.build.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.pcBuilder.build.model.BuildConfiguration;
import pl.pjatk.pcBuilder.build.model.BuildRequest;
import pl.pjatk.pcBuilder.build.service.BuildService;

@RestController
@RequestMapping("/api/builds")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BuildController {
    private final BuildService buildService;
    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    @PostMapping("/generate")
    public ResponseEntity<?> generateBuild(@RequestBody BuildRequest request) {
        try {
            if (request.getBudget() <= 0) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Budżet musi być większy od 0"));
            }
            
            BuildConfiguration config = buildService.generateBuildConfiguration(request);
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("Błąd podczas generowania konfiguracji: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
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
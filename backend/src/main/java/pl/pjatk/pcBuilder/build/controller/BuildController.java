package pl.pjatk.pcBuilder.build.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pl.pjatk.pcBuilder.build.model.BuildConfiguration;
import pl.pjatk.pcBuilder.build.model.BuildRequest;
import pl.pjatk.pcBuilder.build.service.BuildService;

@RestController
@RequestMapping("/api/builds")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BuildController {
    private final BuildService buildService;

    @PostMapping("/generate")
    public ResponseEntity<BuildConfiguration> generateBuild(@RequestBody BuildRequest request) {
        try {
            BuildConfiguration configuration = buildService.generateBuildConfiguration(request);
            return ResponseEntity.ok(configuration);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 
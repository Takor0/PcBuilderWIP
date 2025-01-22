package pl.pjatk.pcBuilder.build.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pl.pjatk.pcBuilder.build.model.BuildConfiguration;
import pl.pjatk.pcBuilder.build.model.BuildRequest;
import pl.pjatk.pcBuilder.build.model.components.Cpu;
import pl.pjatk.pcBuilder.build.model.components.HardDrive;
import pl.pjatk.pcBuilder.build.model.components.Motherboard;
import pl.pjatk.pcBuilder.build.model.components.PcCase;
import pl.pjatk.pcBuilder.build.model.components.PowerSupply;
import pl.pjatk.pcBuilder.build.model.components.VideoCard;
import pl.pjatk.pcBuilder.build.repository.CpuRepository;
import pl.pjatk.pcBuilder.build.repository.HardDriveRepository;
import pl.pjatk.pcBuilder.build.repository.MotherboardRepository;
import pl.pjatk.pcBuilder.build.repository.PcCaseRepository;
import pl.pjatk.pcBuilder.build.repository.PowerSupplyRepository;
import pl.pjatk.pcBuilder.build.repository.VideoCardRepository;
import pl.pjatk.pcBuilder.build.service.BuildService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BuildController {
    private final BuildService buildService;
    private final CpuRepository cpuRepository;
    private final VideoCardRepository videoCardRepository;
    private final MotherboardRepository motherboardRepository;
    private final HardDriveRepository hardDriveRepository;
    private final PowerSupplyRepository powerSupplyRepository;
    private final PcCaseRepository pcCaseRepository;

    @PostMapping("/builds/generate")
    public ResponseEntity<BuildConfiguration> generateBuild(@RequestBody BuildRequest request) {
        try {
            BuildConfiguration configuration = buildService.generateBuildConfiguration(request);
            return ResponseEntity.ok(configuration);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/components/cpus")
    public ResponseEntity<List<Cpu>> getAllCpus() {
        return ResponseEntity.ok(cpuRepository.findAll());
    }

    @GetMapping("/components/gpus")
    public ResponseEntity<List<VideoCard>> getAllGpus() {
        return ResponseEntity.ok(videoCardRepository.findAll());
    }

    @GetMapping("/components/motherboards")
    public ResponseEntity<List<Motherboard>> getAllMotherboards() {
        return ResponseEntity.ok(motherboardRepository.findAll());
    }

    @GetMapping("/components/storage")
    public ResponseEntity<List<HardDrive>> getAllStorage() {
        return ResponseEntity.ok(hardDriveRepository.findAll());
    }

    @GetMapping("/components/power-supplies")
    public ResponseEntity<List<PowerSupply>> getAllPowerSupplies() {
        return ResponseEntity.ok(powerSupplyRepository.findAll());
    }

    @GetMapping("/components/cases")
    public ResponseEntity<List<PcCase>> getAllCases() {
        return ResponseEntity.ok(pcCaseRepository.findAll());
    }
} 
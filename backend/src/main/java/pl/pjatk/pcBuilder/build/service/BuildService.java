package pl.pjatk.pcBuilder.build.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pl.pjatk.pcBuilder.build.model.BudgetAllocation;
import pl.pjatk.pcBuilder.build.model.BuildConfiguration;
import pl.pjatk.pcBuilder.build.model.BuildRequest;
import pl.pjatk.pcBuilder.build.model.components.Cpu;
import pl.pjatk.pcBuilder.build.model.components.HardDrive;
import pl.pjatk.pcBuilder.build.model.components.Motherboard;
import pl.pjatk.pcBuilder.build.model.components.PcCase;
import pl.pjatk.pcBuilder.build.model.components.PowerSupply;
import pl.pjatk.pcBuilder.build.model.components.VideoCard;
import pl.pjatk.pcBuilder.build.model.enums.AdditionalRequirement;
import pl.pjatk.pcBuilder.build.model.enums.BuildType;
import pl.pjatk.pcBuilder.build.model.enums.CpuPreference;
import pl.pjatk.pcBuilder.build.model.enums.GpuPreference;
import pl.pjatk.pcBuilder.build.repository.CpuRepository;
import pl.pjatk.pcBuilder.build.repository.HardDriveRepository;
import pl.pjatk.pcBuilder.build.repository.MotherboardRepository;
import pl.pjatk.pcBuilder.build.repository.PcCaseRepository;
import pl.pjatk.pcBuilder.build.repository.PowerSupplyRepository;
import pl.pjatk.pcBuilder.build.repository.VideoCardRepository;

@Service
@RequiredArgsConstructor
public class BuildService {
    private final CpuRepository cpuRepository;
    private final VideoCardRepository videoCardRepository;
    private final MotherboardRepository motherboardRepository;
    private final HardDriveRepository hardDriveRepository;
    private final PowerSupplyRepository powerSupplyRepository;
    private final PcCaseRepository pcCaseRepository;
    private final ComponentScorer componentScorer;

    public BuildConfiguration generateBuildConfiguration(BuildRequest request) {
        double remainingBudget = request.getBudget();
        BuildType buildType = determineBuildType(request.getBudget());

        // Alokacja 30% budżetu na procesor
        double cpuBudget = remainingBudget * 0.3;
        Cpu selectedCpu = selectCpu(request, cpuBudget, buildType);
        remainingBudget -= selectedCpu.getPrice();

        // Teraz GPU - tutaj wrzucamy najwięcej kasy, 40% budżetu
        double gpuBudget = request.getBudget() * 0.4;
        VideoCard selectedGpu = selectGpu(request, gpuBudget, buildType);
        remainingBudget -= selectedGpu.getPrice();

        // Alokacja 15% budżetu na płytę główną
        double motherboardBudget = request.getBudget() * 0.15;
        Motherboard selectedMotherboard = selectMotherboard(request, selectedCpu, motherboardBudget);
        remainingBudget -= selectedMotherboard.getPrice();

        // Alokacja maksymalnie 15% budżetu na dyski
        double storageBudget = Math.min(remainingBudget, request.getBudget() * 0.15);
        List<HardDrive> selectedStorage = selectStorage(request, storageBudget);
        remainingBudget -= selectedStorage.stream().mapToDouble(HardDrive::getPrice).sum();

        // Zasilacz
        PowerSupply selectedPsu = selectPowerSupply(request, selectedCpu, selectedGpu, remainingBudget);
        remainingBudget -= selectedPsu.getPrice();

        // Wybór obudowy z pozostałego budżetu
        PcCase selectedCase = selectCase(request, selectedMotherboard, remainingBudget);

        // Obliczanie wskaźników wydajności
        Map<String, Integer> performanceScores = calculatePerformanceScores(selectedCpu, selectedGpu, selectedStorage);

        // Generowanie szczegółowych opisów komponentów
        Map<String, String> componentDescriptions = generateComponentDescriptions(
            selectedCpu, selectedGpu, selectedMotherboard, selectedStorage, selectedPsu, selectedCase);

        return BuildConfiguration.builder()
            .cpu(selectedCpu)
            .gpu(selectedGpu)
            .motherboard(selectedMotherboard)
            .storage(selectedStorage)
            .powerSupply(selectedPsu)
            .pcCase(selectedCase)
            .totalPrice(calculateTotalPrice(selectedCpu, selectedGpu, selectedMotherboard, 
                                         selectedStorage, selectedPsu, selectedCase))
            .performanceScores(performanceScores)
            .componentDescriptions(componentDescriptions)
            .build();
    }

    private BuildType determineBuildType(double budget) {
        if (budget >= 8000) {
            return BuildType.HIGH_END; // Full wypas
        } else if (budget >= 4000) {
            return BuildType.BALANCED; // Zbalansowane
        } else {
            return BuildType.BUDGET;
        }
    }

    private Map<String, String> generateComponentDescriptions(
            Cpu cpu, VideoCard gpu, Motherboard motherboard, 
            List<HardDrive> storage, PowerSupply psu, PcCase pcCase) {
        Map<String, String> descriptions = new HashMap<>();
        
        descriptions.put("cpu", String.format(
            "%s %s with %d cores at %.1fGHz (boosts to %.1fGHz). %s",
            cpu.getBrand(), cpu.getName(), cpu.getCoreCount(), 
            cpu.getCoreClock(), cpu.getBoostClock(),
            cpu.isHasGraphics() ? "Includes integrated graphics." : ""));

        descriptions.put("gpu", String.format(
            "%s %s with %dGB VRAM, core clock at %.1fGHz (boosts to %.1fGHz)",
            gpu.getBrand(), gpu.getName(), gpu.getMemory(),
            gpu.getCoreClock(), gpu.getBoostClock()));

        descriptions.put("motherboard", String.format(
            "%s supporting up to %dMHz memory speed. %s",
            motherboard.getName(), (int)motherboard.getMaxMemorySpeed(),
            motherboard.isHasWifi() ? "Includes built-in WiFi." : ""));

        StringBuilder storageDesc = new StringBuilder();
        for (HardDrive drive : storage) {
            storageDesc.append(String.format(
                "%s %dGB %s drive, ",
                drive.getName(), drive.getCapacity(), drive.getType()));
        }
        descriptions.put("storage", storageDesc.substring(0, storageDesc.length() - 2));

        descriptions.put("powerSupply", String.format(
            "%s rated at %dW with %s efficiency",
            psu.getName(), psu.getWattage(), psu.getEfficiency()));

        descriptions.put("case", String.format(
            "%s (%s form factor)%s",
            pcCase.getName(), pcCase.getFormFactor(),
            pcCase.isHasRgb() ? " with RGB lighting" : ""));

        return descriptions;
    }

    private BudgetAllocation allocateBudget(double totalBudget, BuildType buildType) {
        double cpuPercentage, gpuPercentage, motherboardPercentage, 
               storagePercentage, powerSupplyPercentage, casePercentage;
        
        switch (buildType) {
            case HIGH_END:
                cpuPercentage = 0.30;
                gpuPercentage = 0.35;
                motherboardPercentage = 0.15;
                storagePercentage = 0.10;
                powerSupplyPercentage = 0.05;
                casePercentage = 0.05;
                break;
            case BALANCED:
                cpuPercentage = 0.25;
                gpuPercentage = 0.30;
                motherboardPercentage = 0.20;
                storagePercentage = 0.12;
                powerSupplyPercentage = 0.08;
                casePercentage = 0.05;
                break;
            case BUDGET:
                cpuPercentage = 0.25;
                gpuPercentage = 0.25;
                motherboardPercentage = 0.20;
                storagePercentage = 0.15;
                powerSupplyPercentage = 0.10;
                casePercentage = 0.05;
                break;
            default:
                throw new IllegalArgumentException("Nieznany typ buildu");
        }
        
        return new BudgetAllocation(
            totalBudget * cpuPercentage,
            totalBudget * gpuPercentage,
            totalBudget * motherboardPercentage,
            totalBudget * storagePercentage,
            totalBudget * powerSupplyPercentage,
            totalBudget * casePercentage
        );
    }

    private Cpu selectCpu(BuildRequest request, double budget, BuildType buildType) {
        List<Cpu> availableCpus = cpuRepository.findByPriceLessThanEqual(budget);
        
        if (request.getCpuPreference() != CpuPreference.NO_PREFERENCE) {
            availableCpus = availableCpus.stream()
                .filter(cpu -> cpu.getBrand().equalsIgnoreCase(request.getCpuPreference().toString()))
                .collect(Collectors.toList());
        }
        
        return availableCpus.stream()
            .max(Comparator.comparingDouble(cpu -> 
                componentScorer.calculateScore(cpu, request)))
            .orElseThrow(() -> new RuntimeException("No suitable CPU found"));
    }

    private VideoCard selectGpu(BuildRequest request, double budget, BuildType buildType) {
        List<VideoCard> availableGpus = videoCardRepository.findByPriceLessThanEqual(budget);
        
        if (request.getGpuPreference() != GpuPreference.NO_PREFERENCE) {
            availableGpus = availableGpus.stream()
                .filter(gpu -> gpu.getBrand().equalsIgnoreCase(request.getGpuPreference().toString()))
                .collect(Collectors.toList());
        }
        
        return availableGpus.stream()
            .max(Comparator.comparingDouble(gpu -> 
                componentScorer.calculateScore(gpu, request)))
            .orElseThrow(() -> new RuntimeException("No suitable GPU found"));
    }

    private Motherboard selectMotherboard(BuildRequest request, Cpu cpu, double budget) {
        List<Motherboard> availableMotherboards = motherboardRepository.findByPriceLessThanEqual(budget);
        
        availableMotherboards = availableMotherboards.stream()
            .filter(mb -> mb.getSocket().equals(cpu.getSocket()))
            .collect(Collectors.toList());
            
        if (request.getRequirements().contains(AdditionalRequirement.WIFI)) {
            availableMotherboards = availableMotherboards.stream()
                .filter(Motherboard::isHasWifi)
                .collect(Collectors.toList());
        }
        
        return availableMotherboards.stream()
            .max(Comparator.comparingDouble(mb -> 
                componentScorer.calculateScore(mb, request)))
            .orElseThrow(() -> new RuntimeException("No suitable motherboard found"));
    }

    private List<HardDrive> selectStorage(BuildRequest request, double budget) {
        List<HardDrive> storage = new ArrayList<>();
        final double maxBudget = budget;
        
        // Wybór dysku systemowego (priorytet: NVMe)
        List<HardDrive> primaryDrives = hardDriveRepository.findByType("NVMe");
        Optional<HardDrive> primaryDrive = primaryDrives.stream()
            .filter(drive -> drive.getPrice() <= maxBudget)
            .max(Comparator.comparingInt(HardDrive::getCapacity));
            
        if (primaryDrive.isEmpty()) {
            // Alternatywny wybór dysku SSD
            List<HardDrive> ssdDrives = hardDriveRepository.findByType("SSD");
            primaryDrive = ssdDrives.stream()
                .filter(drive -> drive.getPrice() <= maxBudget)
                .max(Comparator.comparingInt(HardDrive::getCapacity));
                
            if (primaryDrive.isEmpty()) {
                throw new RuntimeException("Nie znaleziono odpowiedniego dysku systemowego");
            }
        }
        
        storage.add(primaryDrive.get());
        final double remainingBudget = maxBudget - primaryDrive.get().getPrice();
        
        // Dodatkowy dysk HDD przy dostępnym budżecie
        if (remainingBudget > 0) {
            hardDriveRepository.findByType("HDD").stream()
                .filter(drive -> drive.getPrice() <= remainingBudget)
                .max(Comparator.comparingInt(HardDrive::getCapacity))
                .ifPresent(storage::add);
        }
        
        return storage;
    }

    private PowerSupply selectPowerSupply(BuildRequest request, Cpu cpu, VideoCard gpu, double budget) {
        int requiredWattage = calculateRequiredWattage(cpu, gpu);
        
        List<PowerSupply> availablePowerSupplies = powerSupplyRepository
            .findByWattageGreaterThanEqual(requiredWattage).stream()
            .filter(psu -> psu.getPrice() <= budget)
            .collect(Collectors.toList());
            
        return availablePowerSupplies.stream()
            .max(Comparator.comparingDouble(psu -> 
                componentScorer.calculateScore(psu, request)))
            .orElseThrow(() -> new RuntimeException("No suitable power supply found"));
    }

    private PcCase selectCase(BuildRequest request, Motherboard motherboard, double budget) {
        List<PcCase> availableCases = pcCaseRepository.findByPriceLessThanEqual(budget);
        
        availableCases = availableCases.stream()
            .filter(pcCase -> pcCase.getFormFactor().equals(motherboard.getFormFactor()))
            .collect(Collectors.toList());
            
        if (request.getRequirements().contains(AdditionalRequirement.RGB)) {
            availableCases = availableCases.stream()
                .filter(PcCase::isHasRgb)
                .collect(Collectors.toList());
        }
        
        return availableCases.stream()
            .max(Comparator.comparingDouble(pcCase -> 
                componentScorer.calculateScore(pcCase, request)))
            .orElseThrow(() -> new RuntimeException("No suitable case found"));
    }

    private double calculateTotalPrice(Cpu cpu, VideoCard gpu, Motherboard motherboard,
                                    List<HardDrive> storage, PowerSupply powerSupply, PcCase pcCase) {
        double storagePrice = storage.stream().mapToDouble(HardDrive::getPrice).sum();
        return cpu.getPrice() + gpu.getPrice() + motherboard.getPrice() + 
               storagePrice + powerSupply.getPrice() + pcCase.getPrice();
    }

    private Map<String, Integer> calculatePerformanceScores(Cpu cpu, VideoCard gpu, List<HardDrive> storage) {
        Map<String, Integer> scores = new HashMap<>();
        
        // Ocena wydajności w grach
        int gamingScore = (int) ((gpu.getMemory() * gpu.getBoostClock() * 0.6) + 
                                (cpu.getCoreCount() * cpu.getBoostClock() * 0.4));
        scores.put("gaming", gamingScore);
        
        // Ocena wydajności w zastosowaniach profesjonalnych
        int workstationScore = (int) ((cpu.getCoreCount() * cpu.getBoostClock() * 0.7) + 
                                     (gpu.getMemory() * gpu.getBoostClock() * 0.3));
        scores.put("workstation", workstationScore);
        
        return scores;
    }

    private int calculateRequiredWattage(Cpu cpu, VideoCard gpu) {
        // Dodatkowe 150W na pozostałe komponenty
        return cpu.getTdp() + gpu.getTdp() + 150;
    }
} 
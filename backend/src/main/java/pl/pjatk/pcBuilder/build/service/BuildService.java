package pl.pjatk.pcBuilder.build.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pl.pjatk.pcBuilder.build.model.enums.BuildType;
import pl.pjatk.pcBuilder.build.model.enums.CpuPreference;
import pl.pjatk.pcBuilder.build.model.enums.GpuPreference;
import pl.pjatk.pcBuilder.build.model.enums.ComputerUsage;
import pl.pjatk.pcBuilder.build.repository.CpuRepository;
import pl.pjatk.pcBuilder.build.repository.HardDriveRepository;
import pl.pjatk.pcBuilder.build.repository.MotherboardRepository;
import pl.pjatk.pcBuilder.build.repository.PcCaseRepository;
import pl.pjatk.pcBuilder.build.repository.PowerSupplyRepository;
import pl.pjatk.pcBuilder.build.repository.VideoCardRepository;

@Service
@RequiredArgsConstructor
public class BuildService {
    private static final Logger logger = LoggerFactory.getLogger(BuildService.class);
    private final CpuRepository cpuRepository;
    private final VideoCardRepository videoCardRepository;
    private final MotherboardRepository motherboardRepository;
    private final HardDriveRepository hardDriveRepository;
    private final PowerSupplyRepository powerSupplyRepository;
    private final PcCaseRepository pcCaseRepository;
    private final ComponentScorer componentScorer;

    public BuildConfiguration generateBuildConfiguration(BuildRequest request) {
        logger.info("Rozpoczynam generowanie konfiguracji dla budżetu: {}", request.getBudget());
        logger.info("Preferencje: CPU={}, GPU={}, Użycie={}, Priorytet={}", 
            request.getCpuPreference(), request.getGpuPreference(), 
            request.getUsage(), request.getPriority());

        double remainingBudget = request.getBudget();
        BuildType buildType = determineBuildType(request.getBudget());
        logger.info("Określony typ buildu: {}", buildType);

        // Alokacja 30% budżetu na procesor
        double cpuBudget = remainingBudget * 0.3;
        logger.info("Budżet na CPU: {}", cpuBudget);
        Cpu selectedCpu = selectCpu(request, cpuBudget, buildType);
        if (selectedCpu.getPrice() <= 0) {
            throw new RuntimeException("Nie znaleziono odpowiedniego procesora w podanym budżecie");
        }
        logger.info("Wybrany CPU: {} ({})", selectedCpu.getName(), selectedCpu.getPrice());
        remainingBudget -= selectedCpu.getPrice();

        // Teraz GPU - tutaj wrzucamy najwięcej kasy, 40% z pozostałego budżetu
        double gpuBudget = remainingBudget * 0.4;
        logger.info("Budżet na GPU: {}", gpuBudget);
        VideoCard selectedGpu = selectGpu(request, gpuBudget, buildType);
        if (selectedGpu.getPrice() <= 0) {
            throw new RuntimeException("Nie znaleziono odpowiedniej karty graficznej w podanym budżecie");
        }
        logger.info("Wybrana karta graficzna: {} ({})", selectedGpu.getName(), selectedGpu.getPrice());
        remainingBudget -= selectedGpu.getPrice();

        // Alokacja 15% budżetu na płytę główną
        double motherboardBudget = remainingBudget * 0.15;
        logger.info("Budżet na płytę główną: {}", motherboardBudget);
        Motherboard selectedMotherboard = selectMotherboard(request, selectedCpu, motherboardBudget);
        if (selectedMotherboard.getPrice() <= 0) {
            throw new RuntimeException("Nie znaleziono odpowiedniej płyty głównej w podanym budżecie");
        }
        logger.info("Wybrana płyta główna: {} ({})", selectedMotherboard.getName(), selectedMotherboard.getPrice());
        remainingBudget -= selectedMotherboard.getPrice();

        // Alokacja maksymalnie 15% budżetu na dyski
        double storageBudget = Math.min(remainingBudget, remainingBudget * 0.15);
        logger.info("Budżet na dyski: {}", storageBudget);
        List<HardDrive> selectedStorage = selectStorage(request, storageBudget);
        double storagePrice = selectedStorage.stream().mapToDouble(HardDrive::getPrice).sum();
        logger.info("Wybrane dyski: {} (łącznie: {})", 
            selectedStorage.stream().map(HardDrive::getName).collect(Collectors.joining(", ")), 
            storagePrice);
        remainingBudget -= storagePrice;

        // Zasilacz
        logger.info("Budżet na zasilacz: {}", remainingBudget);
        PowerSupply selectedPsu = selectPowerSupply(request, selectedCpu, selectedGpu, remainingBudget);
        logger.info("Wybrany zasilacz: {} ({})", selectedPsu.getName(), selectedPsu.getPrice());
        remainingBudget -= selectedPsu.getPrice();

        // Wybór obudowy z pozostałego budżetu
        logger.info("Budżet na obudowę: {}", remainingBudget);
        PcCase selectedCase = selectCase(request, selectedMotherboard, remainingBudget);
        logger.info("Wybrana obudowa: {} ({})", selectedCase.getName(), selectedCase.getPrice());

        // Obliczanie wskaźników wydajności
        Map<String, Integer> performanceScores = calculatePerformanceScores(selectedCpu, selectedGpu, selectedStorage);
        logger.info("Obliczone wskaźniki wydajności: {}", performanceScores);

        // Generowanie szczegółowych opisów komponentów
        Map<String, String> componentDescriptions = generateComponentDescriptions(
            selectedCpu, selectedGpu, selectedMotherboard, selectedStorage, selectedPsu, selectedCase);

        double totalPrice = calculateTotalPrice(selectedCpu, selectedGpu, selectedMotherboard, 
                                             selectedStorage, selectedPsu, selectedCase);
        logger.info("Całkowita cena zestawu: {}", totalPrice);

        return BuildConfiguration.builder()
            .cpu(selectedCpu)
            .gpu(selectedGpu)
            .motherboard(selectedMotherboard)
            .storage(selectedStorage)
            .powerSupply(selectedPsu)
            .pcCase(selectedCase)
            .totalPrice(totalPrice)
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
            "%s %s z %d rdzeniami o taktowaniu %.1fGHz (boost do %.1fGHz). %s",
            cpu.getBrand(), cpu.getName(), cpu.getCoreCount(), 
            cpu.getCoreClock(), cpu.getBoostClock(),
            cpu.isHasGraphics() ? "Posiada zintegrowaną grafikę." : ""));

        descriptions.put("gpu", String.format(
            "%s %s z pamięcią %dGB VRAM, taktowanie bazowe %.1fGHz (boost do %.1fGHz)",
            gpu.getBrand(), gpu.getName(), gpu.getMemory(),
            gpu.getCoreClock(), gpu.getBoostClock()));

        descriptions.put("motherboard", String.format(
            "%s obsługująca pamięć do %dMHz. %s",
            motherboard.getName(), (int)motherboard.getMaxMemorySpeed(),
            motherboard.isHasWifi() ? "Posiada wbudowane WiFi." : ""));

        StringBuilder storageDesc = new StringBuilder();
        for (HardDrive drive : storage) {
            storageDesc.append(String.format(
                "%s %dGB typu %s, ",
                drive.getName(), drive.getCapacity(), drive.getType()));
        }
        descriptions.put("storage", storageDesc.substring(0, storageDesc.length() - 2));

        descriptions.put("powerSupply", String.format(
            "%s o mocy %dW z certyfikatem %s",
            psu.getName(), psu.getWattage(), psu.getEfficiency()));

        descriptions.put("case", String.format(
            "%s (format %s)%s",
            pcCase.getName(), pcCase.getFormFactor(),
            pcCase.isHasRgb() ? " z podświetleniem RGB" : ""));

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
        List<Cpu> availableCpus = cpuRepository.findByPriceLessThanEqual(budget).stream()
            .filter(cpu -> !cpu.getName().toLowerCase().contains("threadripper")) // Wykluczamy Threadrippery
            .collect(Collectors.toList());
        logger.info("Znaleziono {} procesorów w budżecie {}", availableCpus.size(), budget);

        if (availableCpus.isEmpty()) {
            throw new RuntimeException("Nie znaleziono procesorów w podanym budżecie");
        }

        if (request.getCpuPreference() != CpuPreference.NO_PREFERENCE) {
            availableCpus = availableCpus.stream()
                .filter(cpu -> cpu.getBrand().equalsIgnoreCase(request.getCpuPreference().toString()))
                .collect(Collectors.toList());
            logger.info("Po filtrowaniu przez preferencję {} zostało {} procesorów", 
                request.getCpuPreference(), availableCpus.size());
        }


        if (availableCpus.isEmpty()) {
            throw new RuntimeException("Nie znaleziono procesorów spełniających preferencje");
        }

        // Filtruj procesory z ceną większą niż 0
        availableCpus = availableCpus.stream()
            .filter(cpu -> cpu.getPrice() > 0)
            .collect(Collectors.toList());
        logger.info("Po filtrowaniu cen > 0 zostało {} procesorów", availableCpus.size());

        if (availableCpus.isEmpty()) {
            throw new RuntimeException("Nie znaleziono procesorów z poprawnymi cenami");
        }

        // Wybierz najlepszy procesor według scorera
        Cpu selectedCpu = availableCpus.stream()
            .max(Comparator.comparingDouble(cpu -> componentScorer.calculateScore(cpu, request)))
            .orElseThrow(() -> new RuntimeException("Nie znaleziono odpowiedniego procesora"));

        logger.info("Wybrany procesor: {} (cena: {}, socket: {})", 
            selectedCpu.getName(), selectedCpu.getPrice(), selectedCpu.getSocket());

        return selectedCpu;
    }

    private VideoCard selectGpu(BuildRequest request, double budget, BuildType buildType) {
        logger.info("Szukam karty graficznej w budżecie: {}", budget);
        List<VideoCard> availableGpus = videoCardRepository.findByPriceLessThanEqual(budget).stream()
            .filter(gpu -> gpu.getPrice() > 0)
            .filter(gpu -> gpu.getMemory() >= 8) // Minimum 8GB VRAM dla gier
            .collect(Collectors.toList());
            
        logger.info("Znaleziono {} kart graficznych w budżecie {}", availableGpus.size(), budget);

        if (request.getGpuPreference() != GpuPreference.NO_PREFERENCE) {
            String brand = request.getGpuPreference() == GpuPreference.AMD ? "AMD" : "NVIDIA";
            availableGpus = availableGpus.stream()
                .filter(gpu -> gpu.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());
            logger.info("Po filtrowaniu przez preferencję {} zostało {} kart", brand, availableGpus.size());
        }

        if (availableGpus.isEmpty()) {
            throw new RuntimeException("Nie znaleziono odpowiedniej karty graficznej w budżecie");
        }

        // Dla gier AAA preferujemy karty z większą ilością pamięci i wyższym zegarem
        VideoCard selectedGpu = availableGpus.stream()
            .max(Comparator.comparingDouble(gpu -> {
                double score = componentScorer.calculateScore(gpu, request);
                if (request.getUsage() == ComputerUsage.GAMING_AAA) {
                    score *= (gpu.getMemory() / 8.0); // Bonus za więcej pamięci
                    score *= (gpu.getPrice() / 100.0); // Bonus za wyższą cenę (lepsze karty są droższe)
                }
                return score;
            }))
            .orElseThrow(() -> new RuntimeException("Nie znaleziono odpowiedniej karty graficznej"));

        logger.info("Wybrana karta graficzna: {} (cena: {}, pamięć: {} GB)", 
            selectedGpu.getName(), selectedGpu.getPrice(), selectedGpu.getMemory());
        return selectedGpu;
    }

    private Motherboard selectMotherboard(BuildRequest request, Cpu cpu, double budget) {
        List<Motherboard> availableMotherboards = motherboardRepository.findByPriceLessThanEqual(budget);
        
        availableMotherboards = availableMotherboards.stream()
            .filter(mb -> mb.getSocket().equals(cpu.getSocket()))
            .collect(Collectors.toList());
        
        return availableMotherboards.stream()
            .max(Comparator.comparingDouble(mb -> 
                componentScorer.calculateScore(mb, request)))
            .orElseThrow(() -> new RuntimeException("No suitable motherboard found"));
    }

    private List<HardDrive> selectStorage(BuildRequest request, double budget) {
        List<HardDrive> storage = new ArrayList<>();
        final double maxBudget = budget;
        logger.info("Szukam dysków w budżecie: {}", maxBudget);
        
        // Wybór dysku systemowego (priorytet: SSD)
        List<HardDrive> allDrives = hardDriveRepository.findAll();
        List<HardDrive> ssdDrives = allDrives.stream()
            .filter(drive -> drive.getFormFactor().equalsIgnoreCase("SSD"))
            .filter(drive -> drive.getPrice() > 0 && drive.getPrice() <= maxBudget)
            .collect(Collectors.toList());
            
        logger.info("Znaleziono {} dysków SSD w budżecie", ssdDrives.size());
        
        Optional<HardDrive> primaryDrive = ssdDrives.stream()
            .max(Comparator.comparingInt(HardDrive::getCapacity));
            
        if (primaryDrive.isEmpty()) {
            logger.error("Nie znaleziono żadnego dysku SSD w budżecie {}", maxBudget);
            throw new RuntimeException("Nie znaleziono odpowiedniego dysku systemowego");
        }
        
        HardDrive selectedPrimaryDrive = primaryDrive.get();
        logger.info("Wybrany dysk systemowy: {} ({} GB, {} PLN)", 
            selectedPrimaryDrive.getName(), 
            selectedPrimaryDrive.getCapacity(),
            selectedPrimaryDrive.getPrice());
            
        storage.add(selectedPrimaryDrive);
        final double remainingBudget = maxBudget - selectedPrimaryDrive.getPrice();
        
        // Dodatkowy dysk HDD przy dostępnym budżecie
        if (remainingBudget > 0) {
            List<HardDrive> hddDrives = allDrives.stream()
                .filter(drive -> !drive.getFormFactor().equalsIgnoreCase("SSD"))
                .filter(drive -> drive.getPrice() > 0 && drive.getPrice() <= remainingBudget)
                .collect(Collectors.toList());
                
            logger.info("Szukam dodatkowego dysku w pozostałym budżecie: {}. Znaleziono {} dysków", 
                remainingBudget, hddDrives.size());
                
            hddDrives.stream()
                .max(Comparator.comparingInt(HardDrive::getCapacity))
                .ifPresent(hdd -> {
                    storage.add(hdd);
                    logger.info("Dodano dodatkowy dysk: {} ({} GB, {} PLN)", 
                        hdd.getName(), hdd.getCapacity(), hdd.getPrice());
                });
        }
        
        return storage;
    }

    private PowerSupply selectPowerSupply(BuildRequest request, Cpu cpu, VideoCard gpu, double budget) {
        int requiredWattage = calculateRequiredWattage(cpu, gpu);
        logger.info("Wymagana moc zasilacza: {}W, budżet: {} PLN", requiredWattage, budget);
        
        List<PowerSupply> availablePsus = powerSupplyRepository.findAll().stream()
            .filter(psu -> psu.getPrice() > 0 && psu.getPrice() <= budget)
            .filter(psu -> psu.getWattage() >= requiredWattage)
            .filter(psu -> psu.getEfficiency().toLowerCase().contains("gold") || 
                          psu.getEfficiency().toLowerCase().contains("platinum") ||
                          psu.getEfficiency().toLowerCase().contains("titanium"))
            .collect(Collectors.toList());
            
        logger.info("Znaleziono {} zasilaczy spełniających wymagania", availablePsus.size());
        
        if (availablePsus.isEmpty()) {
            // Próbujemy znaleźć jakikolwiek zasilacz o odpowiedniej mocy
            availablePsus = powerSupplyRepository.findAll().stream()
                .filter(psu -> psu.getPrice() > 0 && psu.getPrice() <= budget)
                .filter(psu -> psu.getWattage() >= requiredWattage)
                .collect(Collectors.toList());
                
            if (availablePsus.isEmpty()) {
                throw new RuntimeException("Nie znaleziono odpowiedniego zasilacza w budżecie");
            }
        }
        
        PowerSupply selectedPsu = availablePsus.stream()
            .max(Comparator.comparingInt(PowerSupply::getWattage))
            .orElseThrow(() -> new RuntimeException("Nie znaleziono odpowiedniego zasilacza"));
            
        logger.info("Wybrany zasilacz: {} ({}W, {} PLN, {})", 
            selectedPsu.getName(), selectedPsu.getWattage(), selectedPsu.getPrice(), selectedPsu.getEfficiency());
            
        return selectedPsu;
    }

    private PcCase selectCase(BuildRequest request, Motherboard motherboard, double budget) {
        logger.info("Szukam obudowy w budżecie: {} dla płyty: {}", budget, motherboard.getFormFactor());
        List<PcCase> availableCases = pcCaseRepository.findAll().stream()
            .filter(pcCase -> pcCase.getPrice() > 0 && pcCase.getPrice() <= budget)
            .filter(pcCase -> isCompatibleFormFactor(pcCase.getFormFactor(), motherboard.getFormFactor()))
            .collect(Collectors.toList());

        logger.info("Znaleziono {} kompatybilnych obudów w budżecie", availableCases.size());

        if (availableCases.isEmpty()) {
            // Jeśli nie znaleziono obudów, spróbuj bez filtra form factor
            availableCases = pcCaseRepository.findAll().stream()
                .filter(pcCase -> pcCase.getPrice() > 0 && pcCase.getPrice() <= budget)
                .collect(Collectors.toList());
            logger.info("Po złagodzeniu wymagań znaleziono {} obudów", availableCases.size());
        }

        if (availableCases.isEmpty()) {
            throw new RuntimeException("Nie znaleziono odpowiedniej obudowy w budżecie");
        }

        PcCase selectedCase = availableCases.stream()
            .max(Comparator.comparingDouble(pcCase -> componentScorer.calculateScore(pcCase, request)))
            .orElseThrow(() -> new RuntimeException("Nie znaleziono odpowiedniej obudowy"));

        logger.info("Wybrana obudowa: {} ({} PLN, {})", 
            selectedCase.getName(), selectedCase.getPrice(), selectedCase.getFormFactor());

        return selectedCase;
    }

    private boolean isCompatibleFormFactor(String caseFormFactor, String motherboardFormFactor) {
        // Konwertuj na małe litery dla porównania
        caseFormFactor = caseFormFactor.toLowerCase();
        motherboardFormFactor = motherboardFormFactor.toLowerCase();

        // Jeśli obudowa jest ATX, może pomieścić wszystkie mniejsze płyty
        if (caseFormFactor.contains("atx")) {
            return true;
        }

        // Jeśli obudowa jest micro ATX, może pomieścić micro ATX i mini ITX
        if (caseFormFactor.contains("micro") && 
           (motherboardFormFactor.contains("micro") || motherboardFormFactor.contains("mini"))) {
            return true;
        }

        // Jeśli obudowa jest mini ITX, może pomieścić tylko mini ITX
        if (caseFormFactor.contains("mini") && motherboardFormFactor.contains("mini")) {
            return true;
        }

        // W przeciwnym razie wymagamy dokładnego dopasowania
        return caseFormFactor.equals(motherboardFormFactor);
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
        int cpuPower = cpu.getTdp();
        int gpuPower = gpu.getTdp();
        int basePower = 100; // Dla innych komponentów
        int totalPower = cpuPower + gpuPower + basePower;
        return (int) (totalPower * 1.5); // 50% zapasu
    }
} 
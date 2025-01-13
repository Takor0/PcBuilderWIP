package pl.pjatk.pcBuilder.build.service;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pl.pjatk.pcBuilder.build.model.BuildRequest;
import pl.pjatk.pcBuilder.build.model.components.Cpu;
import pl.pjatk.pcBuilder.build.model.components.HardDrive;
import pl.pjatk.pcBuilder.build.model.components.Motherboard;
import pl.pjatk.pcBuilder.build.model.components.PcCase;
import pl.pjatk.pcBuilder.build.model.components.PowerSupply;
import pl.pjatk.pcBuilder.build.model.components.VideoCard;
import pl.pjatk.pcBuilder.build.model.enums.AdditionalRequirement;
import pl.pjatk.pcBuilder.build.model.enums.ComputerUsage;
import pl.pjatk.pcBuilder.build.model.enums.CpuPreference;
import pl.pjatk.pcBuilder.build.model.enums.GpuPreference;

@Component
@RequiredArgsConstructor
public class ComponentScorer {
    // Wagi dla różnych aspektów oceny
    private static final double PRICE_PERFORMANCE_WEIGHT = 0.4;  // Stosunek ceny do wydajności
    private static final double USAGE_WEIGHT = 0.3;             // Dopasowanie do zastosowania
    private static final double PREFERENCE_WEIGHT = 0.2;        // Preferencje użytkownika
    private static final double COMPATIBILITY_WEIGHT = 0.1;     // Kompatybilność z resztą

    public double calculateScore(Object component, BuildRequest request) {
        // Łączymy wszystkie składowe oceny w jedną wartość
        return calculatePricePerformanceScore(component) * PRICE_PERFORMANCE_WEIGHT +
               calculateUsageScore(component, request.getUsage()) * USAGE_WEIGHT +
               calculatePreferenceScore(component, request) * PREFERENCE_WEIGHT +
               calculateCompatibilityScore(component) * COMPATIBILITY_WEIGHT;
    }

    private double calculatePricePerformanceScore(Object component) {
        // Dla każdego komponentu liczymy inaczej
        if (component instanceof Cpu) {
            Cpu cpu = (Cpu) component;
            // Dla CPU ważne są rdzenie i taktowanie
            return (cpu.getCoreCount() * cpu.getCoreClock()) / cpu.getPrice();
        } else if (component instanceof VideoCard) {
            VideoCard gpu = (VideoCard) component;
            // Dla GPU patrzymy na pamięć i taktowanie
            return (gpu.getMemory() * gpu.getCoreClock()) / gpu.getPrice();
        } else if (component instanceof Motherboard) {
            Motherboard mb = (Motherboard) component;
            // Dla płyty głównej głównie szybkość pamięci
            return mb.getMaxMemorySpeed() / mb.getPrice();
        } else if (component instanceof HardDrive) {
            HardDrive drive = (HardDrive) component;
            // Dla dysków pojemność jest kluczowa
            return drive.getCapacity() / drive.getPrice();
        } else if (component instanceof PowerSupply) {
            PowerSupply psu = (PowerSupply) component;
            // Dla zasilacza moc jest najważniejsza
            return psu.getWattage() / psu.getPrice();
        }
        return 0.0;
    }

    private double calculateUsageScore(Object component, ComputerUsage usage) {
        if (component instanceof Cpu) {
            Cpu cpu = (Cpu) component;
            switch (usage) {
                case GAMING_AAA:
                case GAMING_ESPORT:
                    // Do gier ważne jest taktowanie i liczba rdzeni
                    return cpu.getCoreCount() * 0.6 + cpu.getBoostClock() * 0.4;
                case PROGRAMMING:
                case STREAMING:
                    // Do programowania i streamowania więcej rdzeni się przyda
                    return cpu.getCoreCount() * 0.8 + cpu.getBoostClock() * 0.2;
                case GRAPHICS_RENDERING:
                    // Rendering też lubi dużo rdzeni
                    return cpu.getCoreCount() * 0.7 + cpu.getBoostClock() * 0.3;
                case OFFICE_WORK:
                    // Do biura zintegrowana grafika jest plusem
                    return cpu.getCoreClock() * 0.8 + (cpu.isHasGraphics() ? 0.2 : 0);
                default:
                    return 0.0;
            }
        } else if (component instanceof VideoCard) {
            VideoCard gpu = (VideoCard) component;
            switch (usage) {
                case GAMING_AAA:
                    // AAA gry potrzebują mocy
                    return (gpu.getMemory() * 0.4 + gpu.getBoostClock() * 0.6);
                case GAMING_ESPORT:
                    // E-sport woli szybkość
                    return (gpu.getMemory() * 0.3 + gpu.getBoostClock() * 0.7);
                case GRAPHICS_RENDERING:
                    // Rendering potrzebuje dużo VRAM
                    return (gpu.getMemory() * 0.6 + gpu.getBoostClock() * 0.4);
                case STREAMING:
                    // Streaming lubi balans
                    return (gpu.getMemory() * 0.5 + gpu.getBoostClock() * 0.5);
                default:
                    return 0.0;
            }
        } else if (component instanceof Motherboard) {
            Motherboard mb = (Motherboard) component;
            switch (usage) {
                case GAMING_AAA:
                case GAMING_ESPORT:
                case GRAPHICS_RENDERING:
                    // Do wymagających zastosowań szybka pamięć jest ważna
                    return mb.getMaxMemorySpeed() * 0.8 + (mb.isHasWifi() ? 0.2 : 0);
                default:
                    // Do reszty WiFi jest bardziej przydatne
                    return mb.getMaxMemorySpeed() * 0.6 + (mb.isHasWifi() ? 0.4 : 0);
            }
        }
        return 0.0;
    }

    private double calculatePreferenceScore(Object component, BuildRequest request) {
        if (component instanceof Cpu) {
            Cpu cpu = (Cpu) component;
            // Jeśli nie ma preferencji, wszystko jest git
            if (request.getCpuPreference() == CpuPreference.NO_PREFERENCE) {
                return 1.0;
            }
            // Inaczej sprawdzamy czy marka się zgadza
            return cpu.getBrand().equalsIgnoreCase(request.getCpuPreference().toString()) ? 1.0 : 0.0;
        } else if (component instanceof VideoCard) {
            VideoCard gpu = (VideoCard) component;
            if (request.getGpuPreference() == GpuPreference.NO_PREFERENCE) {
                return 1.0;
            }
            return gpu.getBrand().equalsIgnoreCase(request.getGpuPreference().toString()) ? 1.0 : 0.0;
        } else if (component instanceof PcCase) {
            PcCase pcCase = (PcCase) component;
            // Sprawdzamy czy RGB jest wymagane
            if (request.getRequirements().contains(AdditionalRequirement.RGB)) {
                return pcCase.isHasRgb() ? 1.0 : 0.0;
            }
        }
        return 1.0;
    }

    private double calculateCompatibilityScore(Object component) {
        // TODO: Dodać sprawdzanie kompatybilności między komponentami
        return 1.0;
    }
} 
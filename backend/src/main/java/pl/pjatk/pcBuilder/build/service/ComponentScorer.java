package pl.pjatk.pcBuilder.build.service;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pl.pjatk.pcBuilder.build.model.BuildRequest;
import pl.pjatk.pcBuilder.build.model.components.Cpu;
import pl.pjatk.pcBuilder.build.model.components.Motherboard;
import pl.pjatk.pcBuilder.build.model.components.PcCase;
import pl.pjatk.pcBuilder.build.model.components.PowerSupply;
import pl.pjatk.pcBuilder.build.model.components.VideoCard;
import pl.pjatk.pcBuilder.build.model.enums.ComputerUsage;
import pl.pjatk.pcBuilder.build.model.enums.CpuPreference;
import pl.pjatk.pcBuilder.build.model.enums.GpuPreference;

@Component
@RequiredArgsConstructor
public class ComponentScorer {
    // Wagi dla różnych aspektów oceny
    private static final double PRICE_PERFORMANCE_WEIGHT = 0.4;  // Stosunek ceny do wydajności
    private static final double USAGE_WEIGHT = 0.3;             // Dopasowanie do zastosowania
    private static final double BRAND_WEIGHT = 0.3;             // Preferencje marki

    public double calculateScore(Object component, BuildRequest request) {
        double usageScore = calculateUsageScore(component, request.getUsage());
        double pricePerformanceScore = calculatePricePerformanceScore(component);
        double brandScore = calculateBrandScore(component, request);

        return (usageScore * USAGE_WEIGHT) +
               (pricePerformanceScore * PRICE_PERFORMANCE_WEIGHT) +
               (brandScore * BRAND_WEIGHT);
    }

    private double calculateUsageScore(Object component, ComputerUsage usage) {
        if (component instanceof Cpu) {
            Cpu cpu = (Cpu) component;
            switch (usage) {
                case GAMING_AAA:
                case GAMING_ESPORT:
                    // Do gier ważna jest szybkość pojedynczego rdzenia
                    return cpu.getBoostClock() * 0.7 + cpu.getCoreCount() * 0.3;
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
                    return (gpu.getBoostClock() * 0.7 + gpu.getMemory() * 0.3);
                case GRAPHICS_RENDERING:
                    // Rendering potrzebuje dużo pamięci
                    return (gpu.getMemory() * 0.7 + gpu.getBoostClock() * 0.3);
                case STREAMING:
                    // Streaming korzysta z enkodera
                    return (gpu.getBoostClock() * 0.5 + gpu.getMemory() * 0.5);
                case PROGRAMMING:
                case OFFICE_WORK:
                    // Mniej wymagające zastosowania
                    return (gpu.getMemory() * 0.8 + gpu.getBoostClock() * 0.2);
                default:
                    return 0.0;
            }
        } else if (component instanceof Motherboard) {
            Motherboard mb = (Motherboard) component;
            switch (usage) {
                case GAMING_AAA:
                case GAMING_ESPORT:
                    // Gry lubią szybką pamięć
                    return mb.getMaxMemorySpeed() / 5000.0;
                case PROGRAMMING:
                case GRAPHICS_RENDERING:
                    // Więcej slotów pamięci się przyda
                    return mb.getMemorySlots() / 4.0;
                default:
                    return 0.5;
            }
        } else if (component instanceof PowerSupply) {
            PowerSupply psu = (PowerSupply) component;
            // Dla wszystkich zastosowań liczy się efektywność
            return psu.getEfficiency().contains("Gold") ? 1.0 : 0.5;
        } else if (component instanceof PcCase) {
            return 0.5; // Neutralna ocena dla obudowy
        }
        return 0.0;
    }

    private double calculatePricePerformanceScore(Object component) {
        if (component instanceof Cpu) {
            Cpu cpu = (Cpu) component;
            return (cpu.getCoreCount() * cpu.getBoostClock()) / cpu.getPrice();
        } else if (component instanceof VideoCard) {
            VideoCard gpu = (VideoCard) component;
            return (gpu.getMemory() * gpu.getBoostClock()) / gpu.getPrice();
        } else if (component instanceof PowerSupply) {
            PowerSupply psu = (PowerSupply) component;
            return psu.getWattage() / psu.getPrice();
        }
        return 0.5; // Neutralna ocena dla pozostałych komponentów
    }

    private double calculateBrandScore(Object component, BuildRequest request) {
        if (component instanceof Cpu) {
            Cpu cpu = (Cpu) component;
            if (request.getCpuPreference() == CpuPreference.NO_PREFERENCE) {
                return 1.0;
            }
            return cpu.getBrand().equalsIgnoreCase(request.getCpuPreference().toString()) ? 1.0 : 0.0;
        } else if (component instanceof VideoCard) {
            VideoCard gpu = (VideoCard) component;
            if (request.getGpuPreference() == GpuPreference.NO_PREFERENCE) {
                return 1.0;
            }
            return gpu.getBrand().equalsIgnoreCase(request.getGpuPreference().toString()) ? 1.0 : 0.0;
        }
        return 0.5; // Neutralna ocena dla pozostałych komponentów
    }
} 
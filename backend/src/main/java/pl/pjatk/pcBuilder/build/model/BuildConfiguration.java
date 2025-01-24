package pl.pjatk.pcBuilder.build.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pjatk.pcBuilder.build.model.components.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildConfiguration {
    private Cpu cpu;
    private VideoCard gpu;
    private Motherboard motherboard;
    private List<Memory> memory;
    private List<HardDrive> storage;
    private PowerSupply powerSupply;
    private PcCase pcCase;
    private double totalPrice;
    private Map<String, Integer> performanceScores;
    private Map<String, String> componentDescriptions;
} 
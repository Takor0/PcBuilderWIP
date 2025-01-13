package pl.pjatk.pcBuilder.build.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pjatk.pcBuilder.build.model.components.Cpu;
import pl.pjatk.pcBuilder.build.model.components.HardDrive;
import pl.pjatk.pcBuilder.build.model.components.Motherboard;
import pl.pjatk.pcBuilder.build.model.components.PcCase;
import pl.pjatk.pcBuilder.build.model.components.PowerSupply;
import pl.pjatk.pcBuilder.build.model.components.VideoCard;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildConfiguration {
    private Cpu cpu;
    private VideoCard gpu;
    private Motherboard motherboard;
    private List<HardDrive> storage;
    private PowerSupply powerSupply;
    private PcCase pcCase;
    private double totalPrice;
    private Map<String, Integer> performanceScores;
    private Map<String, String> componentDescriptions;
} 
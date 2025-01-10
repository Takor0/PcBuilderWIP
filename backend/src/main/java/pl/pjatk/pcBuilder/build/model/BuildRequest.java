package pl.pjatk.pcBuilder.build.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.pjatk.pcBuilder.build.model.enums.AdditionalRequirement;
import pl.pjatk.pcBuilder.build.model.enums.ComputerUsage;
import pl.pjatk.pcBuilder.build.model.enums.CpuPreference;
import pl.pjatk.pcBuilder.build.model.enums.GpuPreference;
import pl.pjatk.pcBuilder.build.model.enums.PerformancePriority;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildRequest {
    private double budget;
    private CpuPreference cpuPreference;
    private GpuPreference gpuPreference;
    private ComputerUsage usage;
    private PerformancePriority priority;
    private Set<AdditionalRequirement> requirements;
} 
package pl.pjatk.pcBuilder.build.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAllocation {
    private double cpuBudget;
    private double gpuBudget;
    private double motherboardBudget;
    private double storageBudget;
    private double powerSupplyBudget;
    private double caseBudget;
} 
package pl.pjatk.pcBuilder.build.model.components;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Cpu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cpu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String brand;
    private String socket;
    private double price;
    private int coreCount;
    private double coreClock;
    private double boostClock;
    private int tdp;
    private boolean hasGraphics;
} 
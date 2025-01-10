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
@Table(name = "Motherboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Motherboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String socket;
    private String formFactor;
    private double price;
    private int maxMemory;
    private int memorySlots;
    private double maxMemorySpeed;
    private boolean hasWifi;
} 
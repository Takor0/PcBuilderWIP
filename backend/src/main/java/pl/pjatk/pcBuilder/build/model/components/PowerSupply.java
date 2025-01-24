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
@Table(name = "PowerSupply")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PowerSupply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private double price;
    private String type;
    private String efficiency;
    private int wattage;
    private boolean modular;
} 
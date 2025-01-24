package pl.pjatk.pcBuilder.build.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.pjatk.pcBuilder.build.model.components.PowerSupply;

public interface PowerSupplyRepository extends JpaRepository<PowerSupply, Long> {
    List<PowerSupply> findByPriceLessThanEqual(double price);
    List<PowerSupply> findByWattageGreaterThanEqual(int wattage);
    List<PowerSupply> findByModular(boolean modular);
} 
package pl.pjatk.pcBuilder.build.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.pjatk.pcBuilder.build.model.components.HardDrive;

public interface HardDriveRepository extends JpaRepository<HardDrive, Long> {
    List<HardDrive> findByPriceLessThanEqual(double price);
    List<HardDrive> findByType(String type);
    List<HardDrive> findByCapacityGreaterThanEqual(int capacity);
} 
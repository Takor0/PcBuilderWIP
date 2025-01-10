package pl.pjatk.pcBuilder.build.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.pjatk.pcBuilder.build.model.components.Cpu;

public interface CpuRepository extends JpaRepository<Cpu, Long> {
    List<Cpu> findByPriceLessThanEqual(double price);
    List<Cpu> findByBrand(String brand);
    List<Cpu> findBySocket(String socket);
} 
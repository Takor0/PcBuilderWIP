package pl.pjatk.pcBuilder.build.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.pjatk.pcBuilder.build.model.components.Memory;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
    List<Memory> findByPriceLessThanEqual(double price);
    List<Memory> findBySpeedGreaterThanEqual(double speed);
    List<Memory> findByModules(int modules);
    List<Memory> findByCasLatencyLessThanEqual(int casLatency);
}

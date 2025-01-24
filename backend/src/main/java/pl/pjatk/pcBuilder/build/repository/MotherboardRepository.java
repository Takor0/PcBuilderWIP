package pl.pjatk.pcBuilder.build.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.pjatk.pcBuilder.build.model.components.Motherboard;

public interface MotherboardRepository extends JpaRepository<Motherboard, Long> {
    List<Motherboard> findByPriceLessThanEqual(double price);
    List<Motherboard> findBySocket(String socket);
    List<Motherboard> findByFormFactor(String formFactor);
    List<Motherboard> findByHasWifi(boolean hasWifi);
    List<Motherboard> findByMaxMemory(int maxMemory);
    List<Motherboard> findByMemorySlots(int MemorySlots);
} 
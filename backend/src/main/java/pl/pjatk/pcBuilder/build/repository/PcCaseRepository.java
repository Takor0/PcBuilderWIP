package pl.pjatk.pcBuilder.build.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.pjatk.pcBuilder.build.model.components.PcCase;

public interface PcCaseRepository extends JpaRepository<PcCase, Long> {
    List<PcCase> findByPriceLessThanEqual(double price);
    List<PcCase> findByFormFactor(String formFactor);
    List<PcCase> findByHasRgb(boolean hasRgb);
} 
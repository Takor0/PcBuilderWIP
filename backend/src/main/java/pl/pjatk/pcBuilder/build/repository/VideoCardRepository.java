package pl.pjatk.pcBuilder.build.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.pjatk.pcBuilder.build.model.components.VideoCard;

public interface VideoCardRepository extends JpaRepository<VideoCard, Long> {
    List<VideoCard> findByPriceLessThanEqual(double price);
    List<VideoCard> findByBrand(String brand);
    List<VideoCard> findByTdpLessThanEqual(int tdp);
} 
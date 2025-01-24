package pl.pjatk.pcBuilder.build.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.pcBuilder.build.model.Build;

import java.util.List;
import java.util.UUID;

public interface BuildRepository extends JpaRepository<Build, UUID> {
    List<Build> findByUser_Id(Long userId);
}
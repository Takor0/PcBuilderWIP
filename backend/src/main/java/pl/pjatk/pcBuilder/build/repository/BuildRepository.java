package pl.pjatk.pcBuilder.build.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.pcBuilder.build.model.Build;

import java.util.List;

public interface BuildRepository extends JpaRepository<Build, Long> {
    List<Build> findByUserId(Long id);
}

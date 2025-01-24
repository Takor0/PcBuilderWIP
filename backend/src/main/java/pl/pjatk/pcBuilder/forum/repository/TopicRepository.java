package pl.pjatk.pcBuilder.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.pcBuilder.forum.model.Topic;


public interface TopicRepository extends JpaRepository<Topic, Long> {

}
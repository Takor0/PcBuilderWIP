package pl.pjatk.pcBuilder.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.pjatk.pcBuilder.forum.model.Comment;
import pl.pjatk.pcBuilder.forum.model.Topic;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

}
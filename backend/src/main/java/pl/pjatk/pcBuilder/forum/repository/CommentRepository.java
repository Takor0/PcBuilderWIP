package pl.pjatk.pcBuilder.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.pjatk.pcBuilder.forum.model.Comment;

public interface CommentRepository  extends JpaRepository<Comment, Long> {
    int countByTopic_Id(Long topicId);
}

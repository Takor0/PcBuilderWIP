package pl.pjatk.pcBuilder.forum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.pjatk.pcBuilder.forum.dto.CommentCreateRequest;
import pl.pjatk.pcBuilder.forum.model.Comment;
import pl.pjatk.pcBuilder.forum.model.Topic;
import pl.pjatk.pcBuilder.forum.repository.CommentRepository;
import pl.pjatk.pcBuilder.forum.repository.TopicRepository;
import pl.pjatk.pcBuilder.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Transactional
    public Comment createComment(CommentCreateRequest commentRequest) {
        logger.info("Tworzenie komentarza do posta o id: {}", commentRequest.getTopicId());

        if (commentRequest == null) {
            logger.warn("Komentarz jest pusty. Nie mozna utworzyc komentarza.");
            throw new IllegalArgumentException("Comment object cannot be null");
        }
        if (commentRequest.getTopicId() == null) {
            logger.warn("Topic id is null. Nie mozna utworzyc komentarza.");
            throw new IllegalArgumentException("Topic id cannot be null");
        }
        Topic topic = topicRepository.findById(commentRequest.getTopicId()).orElse(null);
        if (topic == null) {
            logger.warn("Nie znaleziono tematu o id: {}", commentRequest.getTopicId());
            throw new RuntimeException("Topic can not be found: " + commentRequest.getTopicId());
        }

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setUser(userRepository.findByUsername("test_user"));
        comment.setTopic(topic);

        commentRepository.save(comment);
        logger.info("Komentarz został pomyślnie zapisany dla tematu o id: {}", topic.getId());
        return comment;
    }

    @Transactional
    public Comment findCommentById(Long id) {
        logger.info("Wyszukiwanie komentarza po id: {}", id);
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            logger.warn("Nie znaleziono komentarza o id: {}", id);
            throw new RuntimeException("Nie znaleziono komentarza " + id);
        }
        return comment;
    }

    @Transactional
    public void deleteComment(Long id) {
        logger.info("Usuwanie komentarza o id: {}", id);
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            logger.warn("Nie znaleziono komentarza o id: {}", id);
            throw new RuntimeException("Nie znaleziono komentarza " + id);
        }
        commentRepository.delete(comment);
        logger.info("Usunieieto komentarz o id: {}", id);

    }

}

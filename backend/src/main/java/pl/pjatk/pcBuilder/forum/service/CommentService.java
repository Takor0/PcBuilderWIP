package pl.pjatk.pcBuilder.forum.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.pjatk.pcBuilder.forum.dto.CommentCreateRequest;
import pl.pjatk.pcBuilder.forum.dto.CommentDTO;
import pl.pjatk.pcBuilder.forum.model.Comment;
import pl.pjatk.pcBuilder.forum.model.Topic;
import pl.pjatk.pcBuilder.forum.repository.CommentRepository;
import pl.pjatk.pcBuilder.forum.repository.TopicRepository;
import pl.pjatk.pcBuilder.user.model.User;
import pl.pjatk.pcBuilder.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Transactional
    public CommentDTO createComment(CommentCreateRequest commentRequest, String username) {
        logger.info("Tworzenie komentarza do posta o id: {}", commentRequest.getTopicId());

        if (commentRequest == null) {
            logger.warn("Komentarz jest pusty. Nie mozna utworzyc komentarza.");
            throw new IllegalArgumentException("Komentarz jest pusty. Nie mozna utworzyc komentarza.");
        }
        if (commentRequest.getTopicId() == null) {
            logger.warn("Topic id is null. Nie mozna utworzyc komentarza.");
            throw new IllegalArgumentException("Topic id cannot be null");
        }

        Topic topic = topicRepository.findById(commentRequest.getTopicId()).orElse(null);
        if (topic == null) {
            logger.warn("Nie znaleziono tematu o id: {}", commentRequest.getTopicId());
            throw new RuntimeException("Nie znaleziono tematu o id: " + commentRequest.getTopicId());
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("Nie znaleziono użytkownika o nazwie: {}", username);
            throw new RuntimeException("Użytkownik nie istnieje.");
        }

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setUser(user);
        comment.setTopic(topic);

        commentRepository.save(comment);
        logger.info("Komentarz został pomyślnie zapisany dla tematu o id: {}", topic.getId());

        return mapToDTO(comment);
    }

    @Transactional
    public CommentDTO findCommentById(Long id) {
        logger.info("Wyszukiwanie komentarza po id: {}", id);
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            logger.warn("Nie znaleziono komentarza o id: {}", id);
            throw new RuntimeException("Nie znaleziono komentarza " + id);
        }
        return mapToDTO(comment);
    }

    @Transactional
    public void deleteComment(Long id, String username) {
        logger.info("Usuwanie komentarza o id: {}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Nie znaleziono komentarza o id: {}", id);
                    return new RuntimeException("Nie znaleziono komentarza, id " + id);
                });

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Nie znaleziono użytkownika: " + username);
        }

        if (!user.getIsAdmin() && !comment.getUser().getUsername().equals(username)) {
            logger.warn("Użytkownik {} nie ma uprawnień do usunięcia komentarza o id: {}", username, id);
            throw new RuntimeException("Nie masz uprawnień do usunięcia tego komentarza.");
        }

        commentRepository.delete(comment);
        logger.info("Usunięto komentarz o id: {}", id);
    }

    public CommentDTO mapToDTO(Comment comment) {
        return new CommentDTO(
                comment.getId(),
                comment.getTopic().getId(),
                comment.getContent(),
                comment.getUser().getUsername(),
                comment.getDateOfCreation().toString()
        );
    }

}

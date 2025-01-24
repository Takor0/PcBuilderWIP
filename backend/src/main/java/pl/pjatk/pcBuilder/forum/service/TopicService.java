package pl.pjatk.pcBuilder.forum.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.pjatk.pcBuilder.forum.dto.CommentDTO;
import pl.pjatk.pcBuilder.forum.dto.TopicCreateRequest;
import pl.pjatk.pcBuilder.forum.dto.TopicDTO;
import pl.pjatk.pcBuilder.forum.model.Topic;
import pl.pjatk.pcBuilder.forum.repository.TopicRepository;
import pl.pjatk.pcBuilder.user.model.User;
import pl.pjatk.pcBuilder.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TopicService {
    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;


    @Transactional
    public TopicDTO createTopic(TopicCreateRequest topicRequest, String username) {
        logger.info("Tworzenie tematu o tytule: {}", topicRequest.getTitle());

        if (topicRequest.getTitle() == null) {
            logger.warn("Bledne dane tematu. Nie można swtowrzyć tematu.");
            throw new IllegalArgumentException("Nieprawidlowe dane tematu");
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Użytkownik nie istnieje.");
        }

        Topic topic = new Topic();
        topic.setUser(user);
        topic.setTitle(topicRequest.getTitle());
        topic.setContent(topicRequest.getContent());
        topic.setLink(topicRequest.getLink());
        topicRepository.save(topic);

        logger.info("Temat stworzony: {}", topic.getTitle());
        return mapToDTO(topic);
    }

    @Transactional
    public TopicDTO getByTopicId(Long id) {
        logger.info("Wyszukiwanie tematu po id: {}", id);
        Topic topic = topicRepository.findById(id).orElse(null);
        if (topic == null) {
            logger.warn("Nie znaleziono tematu o id: {}", id);
            throw new RuntimeException("Nie znaleziono tematu " + id);
        }
        logger.info("Temat znaleziony: {}", id);
        return mapToDTO(topic);
    }

    @Transactional
    public void deleteTopic(Long id, String username) {
        logger.info("Usuwanie tematu o id: {}", id);

        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Nie znaleziono tematu o id: {}", id);
                    return new RuntimeException("Nie znaleziono tematu " + id);
                });

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Nie znaleziono użytkownika: " + username);
        }

        if (!user.getIsAdmin() && !topic.getUser().getUsername().equals(username)) {
            logger.warn("Użytkownik {} nie ma uprawnień do usunięcia tematu o id: {}", username, id);
            throw new RuntimeException("Nie masz uprawnień do usunięcia tego tematu.");
        }

        topicRepository.deleteById(id);
        logger.info("Usunięto temat o id: {}", id);
    }


    @Transactional
    public List<CommentDTO> getAllCommentsFromTopic(Long topicId) {
        logger.info("Fetching all comments for topic with id: {}", topicId);

        Topic topic = topicRepository.findById(topicId).orElse(null);
        if (topic == null) {
            logger.warn("Nie znaleziono tematu z id:" + topicId);
            throw new RuntimeException("Nie znaleziono tematu z id:" + topicId);
        }
        logger.info("Zwrocono {} komentarzy dla tematu o id: {}", topic.getComments().size(), topicId);

        return topic.getComments().stream()
                .map(comment -> commentService.mapToDTO(comment))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TopicDTO> getAllTopics() {
        logger.info("Pobieranie wszystkich postów.");
        List<TopicDTO> topics = topicRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        if (topics.isEmpty()) {
            logger.warn("Brak tematów do wyświetlenia.");
        }
        logger.info("Pobrano wszystkie tematy: {}", topics.size());
        return topics;
    }

    public TopicDTO mapToDTO(Topic topic) {
        return new TopicDTO(
                topic.getId(),
                topic.getTitle(),
                topic.getContent(),
                topic.getUser().getUsername(),
                topic.getDateOfCreation().toString(),
                topic.getComments().size()
        );
    }

}

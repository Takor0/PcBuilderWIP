package pl.pjatk.pcBuilder.forum.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.pjatk.pcBuilder.forum.dto.TopicCreateRequest;
import pl.pjatk.pcBuilder.forum.dto.TopicDTO;
import pl.pjatk.pcBuilder.forum.model.Comment;
import pl.pjatk.pcBuilder.forum.model.Topic;
import pl.pjatk.pcBuilder.forum.repository.CommentRepository;
import pl.pjatk.pcBuilder.forum.repository.TopicRepository;
import pl.pjatk.pcBuilder.user.model.User;
import pl.pjatk.pcBuilder.user.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TopicService {
    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Transactional
    public Topic createPost(TopicCreateRequest topicRequest) {
        logger.info("Tworzenie tematu o tytule: {}", topicRequest.getTitle());

        if (topicRequest == null) {
            logger.warn("Topic object is null. Cannot create topic.");
            throw new IllegalArgumentException("Topic object cannot be null");
        }
        if (topicRequest.getTitle() == null) {
            logger.warn("Topic title is null. Cannot create topic.");
            throw new IllegalArgumentException("Topic title cannot be null");
        }
        Topic topic = new Topic();

        User user = userRepository.findByUsername("test_user");
        if (user == null) {
            logger.warn("Uzytkownik nie istnieje.");
            throw new RuntimeException("Uzytkownik nie istnieje.");
        }
        topic.setUser(user);

        topic.setTitle(topicRequest.getTitle());
        topic.setContent(topicRequest.getContent());
        topic.setLink(topicRequest.getLink());

        logger.info("Temat stworzony: {}", topic.getTitle());
        topicRepository.save(topic);
        return topic;
    }

    @Transactional
    public TopicDTO getByTopicId(Long id) {
        logger.info("Wyszukiwanie tematu po id: {}", id);
        Topic topic = topicRepository.findById(id).orElse(null);
        if (topic == null) {
            logger.warn("Nie znaleziono tematu o id: {}", id);
            throw new RuntimeException("Nie znaleziono tematu " + id);
        }
        int commentCount = commentRepository.countByTopic_Id(id);

        return new TopicDTO(topic.getId(),
                            topic.getTitle(),
                            topic.getUser().getUsername(),
                            commentCount);
    }

    @Transactional
    public void deletePost(Long id) {
        logger.info("Usuwanie tematu o id: {}", id);
        Topic topic = topicRepository.findById(id).orElse(null);
        if (topic == null) {
            logger.warn("Nie znaleziono tematu o id: {}", id);
            throw new RuntimeException("Nie znaleziono tematu " + id);
        }
        topicRepository.deleteById(id);
        logger.info("Usunieieto tematu o id: {}", id);
    }


    @Transactional
    public List<Comment> getAllCommentsFromTopic(Long topicId) {
        logger.info("Fetching all comments for topic with id: {}", topicId);
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("No topic found with id " + topicId));

        if (topic == null) {
            logger.warn("No topic found with id: {}", topicId);
            throw new RuntimeException("No topic found with id " + topicId);
        }

        logger.info("Returning {} comments for topic with id: {}", topic.getComments().size(), topicId);
        return topic.getComments();
    }

    @Transactional
    public List<Topic> getAllTopics() {
        List<Topic> topicList = topicRepository.findAll();
        if (topicList.isEmpty()) {
            throw new RuntimeException("No topics found");
        }
        return topicList;
    }
}
package pl.pjatk.pcBuilder.forum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.pcBuilder.forum.dto.TopicCreateRequest;
import pl.pjatk.pcBuilder.forum.service.TopicService;

@RestController
@RequestMapping("/api/forum/topic")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;
    

    @PostMapping("/create")
    public ResponseEntity<?> createTopic(@RequestBody TopicCreateRequest topic) {
        try {
            return ResponseEntity.ok(topicService.createPost(topic));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTopicById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(topicService.getByTopicId(id));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Topic not found: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTopic(@PathVariable Long id) {
        try {
            topicService.deletePost(id);
            return ResponseEntity.ok("Topic by id: "+ id +"deleted");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    
    

    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getAllCommentsByTopicId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(topicService.getAllCommentsFromTopic(id));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Comments not found for topic id " + id + ": " + e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }
}
package pl.pjatk.pcBuilder.forum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.pcBuilder.forum.dto.TopicCreateRequest;
import pl.pjatk.pcBuilder.forum.dto.TopicDTO;
import pl.pjatk.pcBuilder.forum.service.TopicService;
import pl.pjatk.pcBuilder.user.service.AuthService;

@RestController
@RequestMapping("/api/forum/topic")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TopicController {
    private final TopicService topicService;
    private final AuthService authService;
    

    @PostMapping("/create")
    public ResponseEntity<?> createTopic(@RequestBody TopicCreateRequest topicRequest,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader,
                                         @CookieValue(value = "auth-token", required = false) String cookieToken) {
        try {
            String token;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.replace("Bearer ", "");
            } else if (cookieToken != null) {
                token = cookieToken;
            } else {
                throw new RuntimeException("Token nie został dostarczony");
            }
            String username = authService.validateToken(token);

            TopicDTO topicDTO = topicService.createTopic(topicRequest, username);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(topicDTO);
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
    public ResponseEntity<?> deleteTopic(@PathVariable Long id,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader,
                                         @CookieValue(value = "auth-token", required = false) String cookieToken) {
        try {
            String token;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.replace("Bearer ", "");
            } else if (cookieToken != null) {
                token = cookieToken;
            } else {
                throw new RuntimeException("Token nie został dostarczony");
            }
            String username = authService.validateToken(token);
            topicService.deleteTopic(id, username);
            return ResponseEntity.ok("Topic by id: "+ id + " deleted");
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
                    .body("Topic with given id not found: " + id + ": " + e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllTopics() {
        return ResponseEntity.ok(topicService.getAllTopics());
    }
}
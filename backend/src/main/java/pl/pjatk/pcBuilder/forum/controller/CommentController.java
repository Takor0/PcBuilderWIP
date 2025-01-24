package pl.pjatk.pcBuilder.forum.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.pcBuilder.forum.dto.CommentCreateRequest;
import pl.pjatk.pcBuilder.forum.service.CommentService;
import pl.pjatk.pcBuilder.user.service.AuthService;

@RestController
@RequestMapping("/api/forum/comment")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final AuthService authService;

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody CommentCreateRequest commentRequest,
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

            return ResponseEntity.ok(commentService.createComment(commentRequest, username));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
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

            commentService.deleteComment(id, username);
            return ResponseEntity.ok("Comment by id: "+ id +"deleted");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(commentService.findCommentById(id));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Comment not found: " + e.getMessage());
        }
    }


}

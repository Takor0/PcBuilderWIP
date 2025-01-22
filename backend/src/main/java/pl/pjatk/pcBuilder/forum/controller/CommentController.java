package pl.pjatk.pcBuilder.forum.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pjatk.pcBuilder.forum.dto.CommentCreateRequest;
import pl.pjatk.pcBuilder.forum.service.CommentService;

@RestController
@RequestMapping("/api/forum/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;


    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody CommentCreateRequest comment) {
        try {
            return ResponseEntity.ok(commentService.createComment(comment));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
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

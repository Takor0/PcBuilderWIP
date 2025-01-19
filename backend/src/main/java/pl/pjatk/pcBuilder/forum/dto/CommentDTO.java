package pl.pjatk.pcBuilder.forum.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private Long topicId;
    private String content;
    private String username;
    private String dateOfCreation;
}

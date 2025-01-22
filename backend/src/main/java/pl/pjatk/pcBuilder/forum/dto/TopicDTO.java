package pl.pjatk.pcBuilder.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO {
    private Long id;
    private String title;
    private String content;
    private String username;
    private String dateOfCreation;
    private int commentsCount;
}


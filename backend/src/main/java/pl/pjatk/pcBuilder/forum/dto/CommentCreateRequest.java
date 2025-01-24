package pl.pjatk.pcBuilder.forum.dto;

import lombok.Data;

@Data
public class CommentCreateRequest {
    private Long topicId;
    private String content;
}

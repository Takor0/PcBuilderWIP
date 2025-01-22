package pl.pjatk.pcBuilder.forum.dto;

import lombok.Data;

@Data
public class TopicCreateRequest {
    private String title;
    private String content;
    private String link;
}

package pl.pjatk.pcBuilder.forum.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import pl.pjatk.pcBuilder.user.model.User;

import java.util.Date;


@Entity
@Table(name = "Comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @CreationTimestamp
    private Date dateOfCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonBackReference
    private Topic topic;



    @JsonGetter("topicId")
    public Long getTopicId() {
        return topic != null ? topic.getId() : null;
    }

    @JsonGetter("userId")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", user=" + user.getUsername() +
                '}';
    }


}

package greencity.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @ManyToOne private User user;

    @ManyToOne private Place place;

    @ManyToOne private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> comments = new ArrayList<>();
}

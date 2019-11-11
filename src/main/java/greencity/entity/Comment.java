package greencity.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"comments", "photos"})
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @ManyToOne
    private User user;

    @ManyToOne
    private Place place;

    @ManyToOne
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = {CascadeType.ALL})
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = {CascadeType.ALL})
    private List<Photo> photos = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private Estimate estimate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedDate;
}

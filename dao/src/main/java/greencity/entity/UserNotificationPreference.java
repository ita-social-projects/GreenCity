package greencity.entity;

import greencity.enums.EmailPreference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "user_email_preferences",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "email_preference"}))
public class UserNotificationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_preference")
    private EmailPreference emailPreference;
}

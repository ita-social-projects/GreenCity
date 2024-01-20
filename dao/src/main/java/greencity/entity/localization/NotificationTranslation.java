package greencity.entity.localization;

import greencity.entity.Notification;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "notification_translations")
@SuperBuilder
@EqualsAndHashCode()
@NoArgsConstructor
public class NotificationTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titleUa;
    @Column(nullable = false)
    private String textUa;

    @Column(nullable = false)
    private String titleEng;
    @Column(nullable = false)
    private String textEng;
}

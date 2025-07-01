package greencity.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.ZonedDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"createdAt"})
@ToString(exclude = {"createdAt"})
@Table(name = "daily_fact")
public class DailyFact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

    @Column(name = "fact_en", nullable = false, length = 300)
    private String factEn;

    @Column(name = "fact_uk", nullable = false, length = 300)
    private String factUk;

    @UpdateTimestamp
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
}

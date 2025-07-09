package greencity.entity;

import greencity.converters.FloatArrayConverter;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "eco_news_relevance")
public class EcoNewsRelevance {
    @Id
    @Column(name = "eco_news_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "eco_news_id", referencedColumnName = "id")
    private EcoNews ecoNews;

    @Convert(converter = FloatArrayConverter.class)
    @Column(name = "title_vector", columnDefinition = "text")
    private Float[] titleVector;
}


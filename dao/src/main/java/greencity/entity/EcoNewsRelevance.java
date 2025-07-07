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
    private Long id;

    @OneToOne
    @JoinColumn(name = "eco_news_id", nullable = false, referencedColumnName = "id")
    private EcoNews ecoNews;

    @Convert(converter = FloatArrayConverter.class)
    @Column(name = "title_vector", columnDefinition = "text")
    private Float[] titleVector;

}


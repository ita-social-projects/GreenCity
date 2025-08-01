package greencity.entity;

import greencity.converters.FloatArrayConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

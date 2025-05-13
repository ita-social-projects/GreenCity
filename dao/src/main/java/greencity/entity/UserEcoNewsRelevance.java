package greencity.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "user_eco_news_relevance")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NamedEntityGraph(
    name = "UserEcoNewsRelevance.withEcoNewsAndAuthor",
    attributeNodes = {
        @NamedAttributeNode(value = "ecoNews", subgraph = "ecoNewsGraph")
    },
    subgraphs = {
        @NamedSubgraph(name = "ecoNewsGraph", attributeNodes = {
            @NamedAttributeNode("author"),
            @NamedAttributeNode("tags"),
        })
    }
)
public class UserEcoNewsRelevance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eco_news_id", nullable = false)
    private EcoNews ecoNews;

    @Min(value = 0, message = "greenCity.relevance.min_relevance")
    @Max(value = 1, message = "greenCity.relevance.max_relevance")
    private Double relevance;
}

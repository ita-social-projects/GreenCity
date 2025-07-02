package greencity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@Table(name = "tags_coherence")
public class TagsCoherence {
    @EmbeddedId
    private TagsCoherenceId id;

    @MapsId("sourceTagId")
    @ManyToOne
    @JoinColumn(name = "source_tag_id", referencedColumnName = "id")
    private Tag sourceTag;

    @MapsId("destinationTagId")
    @ManyToOne
    @JoinColumn(name = "destination_tag_id", referencedColumnName = "id")
    private Tag destinationTag;

    @Column(nullable = false)
    private Float coherence;

    @Data
    @Embeddable
    public static class TagsCoherenceId {
        @Column(name = "source_tag_id", nullable = false)
        private Long sourceTagId;

        @Column(name = "destination_tag_id", nullable = false)
        private Long destinationTagId;
    }
}

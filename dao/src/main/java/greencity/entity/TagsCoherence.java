package greencity.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
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

    @Embeddable
    @Data
    public static class TagsCoherenceId {
        @Column(name = "source_tag_id", nullable = false)
        private Long sourceTagId;

        @Column(name = "destination_tag_id", nullable = false)
        private Long destinationTagId;
    }
}

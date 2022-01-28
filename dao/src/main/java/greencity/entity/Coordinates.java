package greencity.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Embeddable
@ToString
@EqualsAndHashCode
public final class Coordinates {
    @Column
    @NonNull
    private double latitude;

    @Column
    @NonNull
    private double longitude;
}

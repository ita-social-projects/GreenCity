package greencity.dto.dailyfact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.EqualsAndHashCode;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(exclude = {"createdAt"})
public class DailyFactVO {

    private Long id;

    private String email;

    private String factEn;

    private String factUk;

    private ZonedDateTime createdAt;
}

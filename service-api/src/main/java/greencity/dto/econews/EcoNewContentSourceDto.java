package greencity.dto.econews;

import javax.validation.constraints.NotEmpty;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@EqualsAndHashCode
public class EcoNewContentSourceDto {
    @NotEmpty
    private String content;

    private String source;
}

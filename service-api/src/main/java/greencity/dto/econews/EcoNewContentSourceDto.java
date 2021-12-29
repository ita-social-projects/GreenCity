package greencity.dto.econews;

import javax.validation.constraints.NotEmpty;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Builder
@EqualsAndHashCode
public class EcoNewContentSourceDto {
    @NotEmpty
    private String content;

    private String source;
}

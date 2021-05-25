package greencity.dto.advice;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AdviceViewDto {
    private String id;
    private String habitId;
    private String translationContent;
}

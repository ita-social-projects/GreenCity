package greencity.dto.specification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class SpecificationVO {
    private Long id;
    private String name;
}

package greencity.dto.category;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryVO {
    private Long id;
    private String name;
}

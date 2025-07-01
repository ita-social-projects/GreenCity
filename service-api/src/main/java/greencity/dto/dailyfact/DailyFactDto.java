package greencity.dto.dailyfact;

import greencity.dto.user.UserVO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class DailyFactDto {
    private Long id;

    @NotNull
    private UserVO userVO;

    @NotBlank
    @Length(max = 300)
    private String factEn;

    @NotBlank
    @Length(max = 300)
    private String factUk;
}

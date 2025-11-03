package greencity.dto.user;

import greencity.dto.language.LanguageDTO;
import greencity.dto.location.UserLocationDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UserVO extends UserManagementVO {
    private UserLocationDto userLocation;

    private LanguageDTO languageVO;
}

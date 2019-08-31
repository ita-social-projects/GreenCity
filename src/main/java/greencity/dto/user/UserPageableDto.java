package greencity.dto.user;

import greencity.entity.enums.ROLE;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPageableDto {
    private List<UserForListDto> page;

    @NotNull
    private long totalElements;

    @NotNull
    private int currentPage;

    private ROLE[] roles;
}

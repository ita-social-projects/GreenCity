package greencity.dto.user;

import greencity.entity.enums.ROLE;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPageableDto {
    @NotNull
    private List<UserForListDto> page;

    @NotNull
    private long totalElements;

    @NotNull
    private int currentPage;

    @NotNull
    private ROLE[] roles;
}

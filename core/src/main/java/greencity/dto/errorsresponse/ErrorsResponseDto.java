package greencity.dto.errorsresponse;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorsResponseDto {
    boolean status;

    List<FieldErrorDto> errors = new ArrayList<>();
}

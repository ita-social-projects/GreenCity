package greencity.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterUserDto {
    @Valid
    private String searchReg;
}

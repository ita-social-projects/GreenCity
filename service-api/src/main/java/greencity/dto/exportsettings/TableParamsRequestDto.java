package greencity.dto.exportsettings;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record TableParamsRequestDto(
    @Pattern(regexp = "^(?!_)[a-z]+(_[a-z]+)*(?<!_)$", message = ErrorMessage.INVALID_TABLE_NAME) String tableName,

    @Min(value = 0, message = ErrorMessage.NEGATIVE_LIMIT) @Max(value = AppConstant.SQL_ROW_LIMIT,
        message = ErrorMessage.EXCEED_LIMIT) int limit,

    @Min(value = 0, message = ErrorMessage.NEGATIVE_OFFSET) int offset) {
}

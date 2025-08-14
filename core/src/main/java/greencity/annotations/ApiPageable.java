package greencity.annotations;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Composite annotation for adding standard pageable query parameters (`page`,
 * `size`, `sort`) to an OpenAPI/Swagger documentation entry.
 * <p>
 * This annotation aggregates multiple {@link Parameter} annotations to describe
 * pagination and sorting options for REST API endpoints in a reusable way,
 * avoiding repetitive parameter documentation.
 * </p>
 *
 * <h2>Parameters added:</h2>
 * <ul>
 * <li><b>page</b> – Page index to retrieve (0-based). Must be ≥ 0. Defaults to
 * {@code 0} if not specified or negative.</li>
 * <li><b>size</b> – Number of records per page. Must be between 1 and 100.
 * Defaults to {@code 5} if not specified, less than 1, or greater than
 * 100.</li>
 * <li><b>sort</b> – Sorting criteria in the format:
 * {@code property,(asc|desc)}. Defaults to ascending order if direction is
 * omitted. Supports multiple sort criteria by repeating the parameter (e.g.,
 * {@code sort=name,asc&sort=age,desc}).</li>
 * </ul>
 *
 * <h2>Usage example:</h2>
 *
 * <pre>
 * {@code
 * &#64;GetMapping("/users")
 * @ApiPageable(clazz = UserDto.class)
 * public Page<UserDto> getUsers(Pageable pageable) {
 *     // ...
 * }
 * }
 * </pre>
 *
 * <h2>Notes:</h2>
 * <ul>
 * <li>The {@code clazz} element is intended to hold the target DTO/entity class
 * so that related validators (e.g., sorting validators) can determine which
 * fields are allowed for sorting.</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
    name = "page",
    schema = @Schema(type = "integer", minimum = "0", defaultValue = "0"),
    in = ParameterIn.QUERY,
    description = "Page index to retrieve [0..N]. Must be an integer greater than or equal to 0. "
        + "If omitted, defaults to 0. Negative or non-numeric values result in 400 Bad Request.")
@Parameter(
    name = "size",
    schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "20"),
    in = ParameterIn.QUERY,
    description = "Number of records per page [1..100]. If omitted, defaults to 20. "
        + "Non-numeric or out-of-range values (less than 1 or greater than 100) result in 400 Bad Request.")
@Parameter(
    name = "sort",
    in = ParameterIn.QUERY,
    description = "Sorting criteria in the format: property,(asc|desc). "
        + "Default sort order is ascending. Supports multiple sort criteria.",
    array = @ArraySchema(schema = @Schema(type = "string", example = "createdDate,desc")),
    style = ParameterStyle.FORM,
    explode = Explode.TRUE)
public @interface ApiPageable {
    /**
     * The class whose fields are relevant for pageable and sortable operations on
     * this endpoint.
     * <p>
     * This is used by sorting validators to restrict sorting to only allowed
     * fields.
     * </p>
     *
     * @return the DTO or entity class associated with the pageable response
     */
    Class<?> clazz();
}

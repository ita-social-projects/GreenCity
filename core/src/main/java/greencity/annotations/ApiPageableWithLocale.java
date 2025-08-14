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
 * Composite annotation for adding pageable query parameters
 * (`lang`, `page`, `size`, `sort`) to OpenAPI/Swagger documentation for
 * REST API endpoints that support both pagination and language-specific content.
 * <p>
 * This annotation works similarly to {@code ApiPageable} but adds an
 * additional {@code lang} parameter for specifying a language code (e.g.,
 * "en", "ua") so that endpoints can return localized results.
 * </p>
 *
 * <h2>Parameters added:</h2>
 * <ul>
 *   <li><b>lang</b> – Language code for the content (string). Determines
 *       the locale in which data should be returned.</li>
 *   <li><b>page</b> – Page index to retrieve (0-based). Must be ≥ 0.
 *       Defaults to {@code 0} if not specified or negative.</li>
 *   <li><b>size</b> – Number of records per page. Must be between 1 and 100.
 *       Defaults to {@code 5} if not specified, less than 1, or greater than 100.</li>
 *   <li><b>sort</b> – Sorting criteria in the format:
 *       {@code property,(asc|desc)}. Defaults to ascending order if direction
 *       is omitted. Supports multiple sort criteria by repeating the parameter
 *       (e.g., {@code sort=name,asc&sort=age,desc}).</li>
 * </ul>
 *
 * <h2>Usage example:</h2>
 * <pre>{@code
 * @GetMapping("/articles")
 * @ApiPageableWithLocale(clazz = ArticleDto.class)
 * public Page<ArticleDto> getArticles(
 *         @RequestParam String lang,
 *         Pageable pageable
 * ) {
 *     // Implementation that returns localized, paginated articles
 * }
 * }</pre>
 *
 * <h2>Notes:</h2>
 * <ul>
 *   <li>The {@code clazz} element specifies the DTO or entity class that
 *       represents the paginated response data. It may also be used by sorting
 *       validators to determine which fields are allowed for sorting.</li>
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        name = "lang",
        description = "Code of the needed language.",
    schema = @Schema(type = "string"),
        in = ParameterIn.QUERY
)
@Parameter(
        name = "page",
        schema = @Schema(type = "integer", minimum = "0", defaultValue = "0"),
        in = ParameterIn.QUERY,
    description = "Page index you want to retrieve [0..N]. "
        + "If page index is less than 0 or not specified then default value is used!"
)
@Parameter(
        name = "size",
        schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "5"),
    in = ParameterIn.QUERY,
        description = "Number of records per page [1..100]. "
        + "If size is less than 1 or not specified then default value is used!"
        + "If size is bigger than 100, size becomes 100."
)
@Parameter(
        name = "sort",
        in = ParameterIn.QUERY,
        description = "Sorting criteria in the format: property,(asc|desc). "
                + "Default sort order is ascending. Supports multiple sort criteria.",
        array = @ArraySchema(schema = @Schema(type = "string")),
        style = ParameterStyle.FORM,
        explode = Explode.TRUE
)
public @interface ApiPageableWithLocale {
    /**
     * The class whose fields are relevant for pageable
     * and sortable operations on this endpoint.
     * <p>
     * This is used by sorting validators to restrict
     * sorting to only allowed fields.
     * </p>
     *
     * @return the DTO or entity class associated with the pageable response
     */
    Class<?> clazz();
}

package greencity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class or specific fields as allowed for sorting in pageable queries.
 * <p>
 * This annotation is typically processed by a sorting validation mechanism (for
 * example, a class like {@code SortPageableValidator}) to determine which
 * properties of a resource can be used in the {@code sort} parameter of API
 * requests.
 * </p>
 *
 * <h2>Usage patterns:</h2>
 * <ul>
 * <li><b>Class-level with explicit fields:</b> <br>
 * Annotate a class and specify the {@link #fields()} array to explicitly
 * declare the only properties that can be sorted.
 *
 * <pre>{@code
 * @Sortable(fields = {"name", "age"})
 * public class PersonDto {
 *     private String name;
 *     private int age;
 *     private String city;
 * }
 * }</pre>
 *
 * </li>
 * <li><b>Class-level without fields:</b> <br>
 * Annotate a class without specifying {@link #fields()} to allow individual
 * field-level {@code @Sortable} annotations to determine sortability.
 *
 * <pre>
 * {
 *     &#64;code
 *     &#64;Sortable
 *     public class PersonDto {
 *         &#64;Sortable
 *         private String name;
 *         &#64;Sortable
 *         private int age;
 *         private String city; // not sortable
 *     }
 * }
 * </pre>
 *
 * </li>
 * <li><b>Fallback behavior:</b> <br>
 * If a class is annotated with {@code @Sortable} but no fields are specified at
 * the class or field level, all declared fields will be considered sortable.
 * </li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Sortable {
    /**
     * Defines the set of allowed sortable fields when applied at the class level.
     * <p>
     * If left empty, the validator will:
     * </p>
     * <ol>
     * <li>Check for field-level {@code @Sortable} annotations, or</li>
     * <li>If none found, allow all declared fields for sorting.</li>
     * </ol>
     *
     * @return an array of field names allowed for sorting
     */
    String[] fields() default {};
}

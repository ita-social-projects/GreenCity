package greencity.mapping;

/**
 * The interface provides method declaration for mapping entity classes to dto classes and vise
 * versa.
 *
 * @param <E> entity class type
 * @param <D> dto class type
 */
public interface MapperToDto<E, D> {
    /**
     * Converts an entity object to dto object.
     *
     * @param entity to map from
     * @return a mapped object with type {@code D}
     */
    D convertToDto(E entity);
}

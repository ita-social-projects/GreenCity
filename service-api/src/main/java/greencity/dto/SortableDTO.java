package greencity.dto;

import greencity.annotations.SortableField;

/**
 * Marker interface to indicate that a DTO is sortable.
 * <p>
 * Classes implementing this interface can be used in pageable API requests
 * where sorting is required. This interface acts as a signal to the application
 * that the associated DTO supports sorting by fields marked with the
 * {@link SortableField} annotation.
 * </p>
 * <p>
 * To enable sorting on a DTO, the class must implement this interface and the
 * relevant fields must be annotated with {@link SortableField}. This ensures
 * that only valid fields are considered for sorting in pageable requests.
 * </p>
 */
public interface SortableDTO {
}

package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when we get metadata from DB and SQLException throws.
 */
@StandardException
public class DatabaseMetadataException extends RuntimeException {
}

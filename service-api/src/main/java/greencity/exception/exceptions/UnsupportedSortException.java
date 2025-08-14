package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception thrown when a client requests sorting by a field
 * that is not allowed for the targeted resource.
 */
@StandardException
public class UnsupportedSortException extends RuntimeException{
}

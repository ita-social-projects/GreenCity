package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that we get when we create excel or other file and IOException
 * thrown.
 */
@StandardException
public class FileGenerationException extends RuntimeException {
}

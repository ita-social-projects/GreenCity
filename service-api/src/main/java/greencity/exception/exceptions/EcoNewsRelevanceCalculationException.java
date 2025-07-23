package greencity.exception.exceptions;

import lombok.experimental.StandardException;

/**
 * Exception that is thrown when relevance cannot be calculated due to wrong parameters or conditions.
 * Exception's message contains main reason.
 *
 * @author Rostyslav Zadyraichuk
 */
@StandardException
public class EcoNewsRelevanceCalculationException extends RuntimeException {
}

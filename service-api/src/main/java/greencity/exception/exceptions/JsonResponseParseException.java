package greencity.exception.exceptions;

public class JsonResponseParseException extends RuntimeException {
  public JsonResponseParseException(String message) {
    super(message);
  }

  public JsonResponseParseException(String message, Throwable cause) {
    super(message, cause);
  }
}

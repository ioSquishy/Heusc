package heusc.API.OpenAi;

public class IncompleteException extends RuntimeException {
    public IncompleteException() {
        super();
    }

    public IncompleteException(String message) {
        super(message);
    }
}

package ksp.vilius.reddit.exceptions;

public class SpringRedditException extends RuntimeException {
    public SpringRedditException(String message,Exception exception) {
        super(message);
    }
    public SpringRedditException(String message) {
        super(message);
    }
}

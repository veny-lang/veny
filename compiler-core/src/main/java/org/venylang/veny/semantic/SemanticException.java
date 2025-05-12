package org.venylang.veny.semantic;

public class SemanticException extends RuntimeException{

    public SemanticException(String message) {
        super(message);
    }

    public SemanticException(String message, Throwable cause) {
        super(message, cause);
    }

}

package org.makson.exception;

public class ParameterNotFoundException extends Exception {
    public ParameterNotFoundException() {
        super();
    }

    public ParameterNotFoundException(String message) {
        super(message);
    }

    public ParameterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterNotFoundException(Throwable cause) {
        super(cause);
    }

    protected ParameterNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

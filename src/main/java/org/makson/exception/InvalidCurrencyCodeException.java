package org.makson.exception;

public class InvalidCurrencyCodeException extends Exception {
    public InvalidCurrencyCodeException() {
        super();
    }

    public InvalidCurrencyCodeException(String message) {
        super(message);
    }

    public InvalidCurrencyCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCurrencyCodeException(Throwable cause) {
        super(cause);
    }

    protected InvalidCurrencyCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

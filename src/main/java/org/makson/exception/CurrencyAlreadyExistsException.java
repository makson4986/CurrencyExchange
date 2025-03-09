package org.makson.exception;

public class CurrencyAlreadyExistsException extends Exception {
    public CurrencyAlreadyExistsException() {
        super();
    }

    public CurrencyAlreadyExistsException(String message) {
        super(message);
    }

    public CurrencyAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    protected CurrencyAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package org.makson.exception;

public class CurrencyAlreadyExistException extends Exception {
    public CurrencyAlreadyExistException() {
        super();
    }

    public CurrencyAlreadyExistException(String message) {
        super(message);
    }

    public CurrencyAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyAlreadyExistException(Throwable cause) {
        super(cause);
    }

    protected CurrencyAlreadyExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

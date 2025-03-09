package org.makson.exception;

public class CurrencyNotFoundException extends Exception {
    public CurrencyNotFoundException() {
        super();
    }

    public CurrencyNotFoundException(String message) {
        super(message);
    }

    public CurrencyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyNotFoundException(Throwable cause) {
        super(cause);
    }

    protected CurrencyNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

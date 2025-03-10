package org.makson.exception;

public class CurrencyCodeMissingException extends RuntimeException {
    public CurrencyCodeMissingException() {
        super();
    }

    public CurrencyCodeMissingException(String message) {
        super(message);
    }

    public CurrencyCodeMissingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyCodeMissingException(Throwable cause) {
        super(cause);
    }

    protected CurrencyCodeMissingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

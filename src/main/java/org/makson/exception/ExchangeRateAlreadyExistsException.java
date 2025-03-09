package org.makson.exception;

public class ExchangeRateAlreadyExistsException extends Exception {
    public ExchangeRateAlreadyExistsException() {
        super();
    }

    public ExchangeRateAlreadyExistsException(String message) {
        super(message);
    }

    public ExchangeRateAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeRateAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    protected ExchangeRateAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

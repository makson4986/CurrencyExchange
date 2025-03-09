package org.makson.exception;

public class ExchangeRateNotFoundException extends Exception {
    public ExchangeRateNotFoundException() {
        super();
    }

    public ExchangeRateNotFoundException(String message) {
        super(message);
    }

    public ExchangeRateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeRateNotFoundException(Throwable cause) {
        super(cause);
    }

    protected ExchangeRateNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

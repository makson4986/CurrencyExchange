package org.makson.exception;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException() {
        super();
    }

    public CurrencyNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

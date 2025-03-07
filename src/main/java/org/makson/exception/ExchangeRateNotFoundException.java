package org.makson.exception;

public class ExchangeRateNotFoundException extends RuntimeException {
    public ExchangeRateNotFoundException() {
      super();
    }

    public ExchangeRateNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

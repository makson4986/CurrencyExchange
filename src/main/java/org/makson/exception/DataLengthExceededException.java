package org.makson.exception;

public class DataLengthExceededException extends Exception {
    public DataLengthExceededException() {
        super();
    }

    public DataLengthExceededException(String message) {
        super(message);
    }

    public DataLengthExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataLengthExceededException(Throwable cause) {
        super(cause);
    }

    protected DataLengthExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

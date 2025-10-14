package com.bakuard.collections.exception;

/**
 * Указывает на попытку задать отрицательный размер при создании структуры данных и увеличении её размера
 * на произвольную величину.
 */
public class NegativeSizeException extends RuntimeException {

    public NegativeSizeException() {
    }

    public NegativeSizeException(String message) {
        super(message);
    }

    public NegativeSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NegativeSizeException(Throwable cause) {
        super(cause);
    }

    public NegativeSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package com.bakuard.collections.exception;

/**
 * Указывает на попытку задать отрицательный или нулевой размер при создании структуры данных или увеличении её размера
 * на произвольную величину.
 */
public class NotPositiveSizeException extends RuntimeException {

    public NotPositiveSizeException() {
    }

    public NotPositiveSizeException(String message) {
        super(message);
    }

    public NotPositiveSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotPositiveSizeException(Throwable cause) {
        super(cause);
    }

    public NotPositiveSizeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

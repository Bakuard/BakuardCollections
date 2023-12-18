package com.bakuard.collections.exceptions;

/**
 * Указывает, что произошла попытка добавить элемент в структуру данных, для которой установлен максимально возможный
 * размер, и при этом текущее кол-во элементов равно максимально возможному.
 */
public class MaxSizeExceededException extends RuntimeException {

    public MaxSizeExceededException() {
    }

    public MaxSizeExceededException(String message) {
        super(message);
    }

    public MaxSizeExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public MaxSizeExceededException(Throwable cause) {
        super(cause);
    }

    public MaxSizeExceededException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

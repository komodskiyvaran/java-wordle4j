package ru.yandex.practicum;

import java.io.IOException;

public class DictionaryLoadException extends RuntimeException  {
    public DictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

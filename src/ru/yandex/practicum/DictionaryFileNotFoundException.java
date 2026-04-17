package ru.yandex.practicum;

import java.io.IOException;

public class DictionaryFileNotFoundException extends RuntimeException {
    public DictionaryFileNotFoundException(String path) {
        super("Файл словаря не найден: " + path);
    }
}

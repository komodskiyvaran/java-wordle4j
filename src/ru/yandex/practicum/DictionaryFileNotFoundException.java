package ru.yandex.practicum;

public class DictionaryFileNotFoundException extends RuntimeException {
    public DictionaryFileNotFoundException(String path) {
        super("Файл словаря не найден: " + path);
    }
}
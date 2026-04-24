package ru.yandex.practicum;

public class EmptyDictionaryException extends RuntimeException {
    public EmptyDictionaryException(String path) {
        super("Словарь пуст или не содержит слов из 5 букв: " + path);
    }
}
package ru.yandex.practicum;

public class GameAlreadyFinishedException extends Exception {
    public GameAlreadyFinishedException(String message) {
        super(message);
    }
}
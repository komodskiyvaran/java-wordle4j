package ru.yandex.practicum;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {
    private static final String DICTIONARY_PATH = "words_ru.txt";
    private static final String LOG_PATH = "game.log";

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(LOG_PATH, StandardCharsets.UTF_8)) {
            try {
                WordleDictionaryLoader loader = new WordleDictionaryLoader();
                WordleDictionary dictionary = loader.getListWords(DICTIONARY_PATH);

                WordleGame game = new WordleGame(dictionary, log);
                runGame(game, log);
            } catch (DictionaryFileNotFoundException |
                     EmptyDictionaryException e) {
                System.err.println("Ошибка загрузки словаря: " + e.getMessage());
                log.println("Ошибка загрузки словаря: " + e.getMessage());
                e.printStackTrace(log);
            } catch (Exception e) {
                System.err.println("Неизвестная ошибка: " + e.getMessage());
                log.println("Неизвестная ошибка: " + e.getMessage());
                e.printStackTrace(log);
            }
        } catch (IOException e) {
            System.err.println("Ошибка создания лог-файла: " + e.getMessage());
        }
    }

    public static void runGame(WordleGame game, PrintWriter log) {
        Scanner scan = new Scanner(System.in);
        System.out.println("Введи слово из 5 букв");
        while (!game.isFinished()) {
            String word = scan.nextLine().trim();
            try {
                if (word.isEmpty()) {
                    String hint = game.getHint();
                    System.out.println(hint);
                    word = hint;
                }

                String mask = game.makeGuess(word);
                System.out.println(mask);

            } catch (WordNotFoundInDictionaryException e) {
                System.out.println("Слово не было найдено в словаре!");
                log.println("Ошибка: слово не найдено в словаре - " + word);
            } catch (InvalidWordLengthException e) {
                System.out.println("Слово должно состоять из 5 букв.");
                log.println("Ошибка: неверная длина слова - " + word);
            } catch (GameAlreadyFinishedException e) {
                System.out.println("Игра уже завершена!");
                log.println("Ошибка: попытка хода после завершения игры");
                break;
            }
        }

        if (game.isWin()) {
            System.out.println("Поздравляем! Вы угадали слово: " + game.getAnswer());
        } else {
            System.out.println("Вы проиграли. Загаданное слово: " + game.getAnswer());
            log.println("Проигрыш, попытки кончились");
        }
    }
}
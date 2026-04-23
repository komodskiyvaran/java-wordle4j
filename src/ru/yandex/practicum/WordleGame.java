package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;

import static ru.yandex.practicum.WordleDictionary.WORD_LENGTH;

public class WordleGame {
    private static final int MAX_STEPS = 6;
    private final List<String> availableWords;
    private int steps;
    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final String answer;
    private boolean win;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        steps = MAX_STEPS;
        answer = dictionary.getRandomWord();
        this.dictionary = dictionary;
        this.log = log;
        this.win = false;
        availableWords = new ArrayList<>(dictionary.getWords());
        log.println("Игра началась. Загадано слово: " + answer);
        log.println("Словарь загружен. Доступно слов: " + availableWords.size());
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter log, String answer) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = answer;
        this.steps = MAX_STEPS;
        this.win = false;
        this.availableWords = new ArrayList<>(dictionary.getWords());
    }

    public String getAnswer() {
        return answer;
    }

    public int getSteps() {
        return steps;
    }

    public boolean isWin() {
        return win;
    }

    public boolean isFinished() {
        return win || steps == 0;
    }

    /**
     * Обработка хода, возвращает маску
     *
     * @param word введенное пользователем слово
     */
    public String makeGuess(String word) throws WordNotFoundInDictionaryException, InvalidWordLengthException, GameAlreadyFinishedException {
        if (isFinished()) {
            throw new GameAlreadyFinishedException("Игра уже завершена!");
        }

        if (word.length() != WORD_LENGTH) {
            throw new InvalidWordLengthException("Слово состоит не из 5 букв!");
        }
        if (!dictionary.getWords().contains(word)) {
            throw new WordNotFoundInDictionaryException("Слово не найдено в словаре!");
        }
        steps--;
        String mask = WordleDictionary.wordComparison(answer, word);

        availableWords.remove(word);
        filterAvailableWords(word, mask);

        log.println("Ход " + (MAX_STEPS - steps) + ": " + word + " -> " + mask);
        if (mask.equals("+++++")) {
            win = true;
            log.println("Победа за " + (MAX_STEPS - steps) + " шагов.");

        }

        return mask;
    }

    /**
     * выдача подсказки из фильтрованного словаря
     */
    public String getHint() throws GameAlreadyFinishedException {
        if (isFinished()) {
            throw new GameAlreadyFinishedException("Игра уже завершена!");
        }

        String hint = availableWords.get(new Random().nextInt(availableWords.size()));
        availableWords.remove(hint);
        log.println("Выдана подсказка: " + hint);
        return hint;
    }

    /**
     * фильтрация словаря availableWords на основе маски
     *
     * @param word введенное пользователем слово
     * @param mask маска введенного слова
     */
    private void filterAvailableWords(String word, String mask) {
        Set<Character> presentChars = new HashSet<>();
        for (int i = 0; i < WORD_LENGTH; i++) {
            char m = mask.charAt(i);
            if (m == '+' || m == '^') {
                presentChars.add(word.charAt(i));
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            final int index = i;
            char c = word.charAt(index);
            char m = mask.charAt(index);

            if (m == '+') {
                availableWords.removeIf(w -> w.charAt(index) != c);
            } else if (m == '^') {
                availableWords.removeIf(w -> w.charAt(index) == c);
                availableWords.removeIf(w -> w.indexOf(c) == -1);
            } else if (m == '-') {
                if (!presentChars.contains(c)) {
                    availableWords.removeIf(w -> w.indexOf(c) != -1);
                } else {
                    availableWords.removeIf(w -> w.charAt(index) == c);
                }
            }
        }
        log.println("Словарь был отфильтрован, доступно: " + availableWords.size() + " слов");
    }
}
package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WordleDictionary {
    public static final int WORD_LENGTH = 5;
    private final Random random = new Random();
    private final List<String> words;

    public WordleDictionary(List<String> words) {
        this.words = words;
    }

    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    public String getRandomWord() {
        return words.get(random.nextInt(words.size()));
    }

    public static String wordComparison(String answer, String word) {
        char[] answerChars = answer.toCharArray();
        char[] result = new char[WORD_LENGTH];

        Arrays.fill(result, '-');

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (word.charAt(i) == answerChars[i]) {
                result[i] = '+';
                answerChars[i] = '#';
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] == '-') {
                for (int j = 0; j < WORD_LENGTH; j++) {
                    if (answerChars[j] == word.charAt(i)) {
                        result[i] = '^';
                        answerChars[j] = '#';
                        break;
                    }
                }
            }
        }
        return new String(result);
    }

    public static String normalize(String raw) {
        return raw.trim().toLowerCase().replace('ё', 'е');
    }
}
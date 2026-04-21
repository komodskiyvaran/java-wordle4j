package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WordleDictionary {
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
        char[] result = new char[5];

        Arrays.fill(result, '-');

        for (int i = 0; i < 5; i++) {
            if (word.charAt(i) == answerChars[i]) {
                result[i] = '+';
                answerChars[i] = '#';
            }
        }

        for (int i = 0; i < 5; i++) {
            if (result[i] == '-') {
                for (int j = 0; j < 5; j++) {
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
}
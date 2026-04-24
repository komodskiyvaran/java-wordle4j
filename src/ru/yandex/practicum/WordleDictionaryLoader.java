package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import static ru.yandex.practicum.WordleDictionary.WORD_LENGTH;

public class WordleDictionaryLoader {

    public WordleDictionary getListWords(String path) {
        List<String> listWords = new ArrayList<>();

        try (FileReader reader = new FileReader(path, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            while ((line = br.readLine()) != null) {
                String word = WordleDictionary.normalize(line);
                if (word.length() == WORD_LENGTH) {
                    listWords.add(word);
                }
            }

            if (listWords.isEmpty()) {
                throw new EmptyDictionaryException(path);
            }

            return new WordleDictionary(listWords);
        } catch (FileNotFoundException e) {
            throw new DictionaryFileNotFoundException(path);
        } catch (IOException e) {
            throw new DictionaryLoadException("Ошибка чтения словаря: " + path, e);
        }
    }
}
package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.WordleDictionary.WORD_LENGTH;


class WordleTest {
    private WordleDictionary dictionary;
    private PrintWriter log;
    private List<String> testWords;

    @BeforeEach
    void setUp() {
        testWords = Arrays.asList("абзац", "агент", "аборт", "аванс", "авгур", "арбуз", "актер", "мытье");
        dictionary = new WordleDictionary(testWords);
        log = new PrintWriter(System.out);
    }

    /**
     * тесты корректности масок
     */

    @Test
    void wordComparisonEqualsPartly() {
        assertEquals("^----", WordleDictionary.wordComparison("радио", "абзац"));
    }

    @Test
    void wordComparisonEqualsTrue() {
        assertEquals("+++++", WordleDictionary.wordComparison("супер", "супер"));
    }

    @Test
    void wordComparisonEqualsFalse() {
        assertEquals("-----", WordleDictionary.wordComparison("радио", "супец"));
    }

    @Test
    void wordComparisonEqualsTwoChar() {
        assertEquals("-+^-+", WordleDictionary.wordComparison("замок", "казак"));
    }

    /**
     * тесты валидации
     */
    @Test
    void makeGuess_WordTooShort_ThrowsInvalidWordLengthException() {
        WordleGame game = new WordleGame(dictionary, log, "абзац");
        assertThrows(InvalidWordLengthException.class, () -> {
            game.makeGuess("кот");
        });
    }

    @Test
    void makeGuess_WordTooLong_ThrowsInvalidWordLengthException() {
        WordleGame game = new WordleGame(dictionary, log, "абзац");
        assertThrows(InvalidWordLengthException.class, () -> {
            game.makeGuess("котофей");
        });
    }

    @Test
    void makeGuess_WordNotInDictionary_ThrowsWordNotFoundInDictionaryException() {
        WordleGame game = new WordleGame(dictionary, log, "абзац");
        assertThrows(WordNotFoundInDictionaryException.class, () -> {
            game.makeGuess("вайва");
        });
    }

    @Test
    void makeGuess_EmptyString_ThrowsInvalidWordLengthException() {
        WordleGame game = new WordleGame(dictionary, log, "абзац");
        assertThrows(InvalidWordLengthException.class, () -> {
            game.makeGuess("");
        });
    }

    @Test
    void makeGuess_AfterGameFinished_ThrowsException() throws Exception {
        WordleGame game = new WordleGame(dictionary, log, "абзац");
        game.makeGuess("абзац");

        assertThrows(GameAlreadyFinishedException.class, () -> game.makeGuess("агент"));
    }

    @Test
    void loadFromFile_NormalizesAndFiltersWords(@TempDir Path tempDir) throws IOException {
        Path testFile = tempDir.resolve("test_dict_words.txt");
        List<String> words = List.of(" оттёк  ", "МЁД", "ещЁ", "", "актёр", "МытЬё", "АБЗАЦ", "    дом   ", "длинное слово");

        Files.write(testFile, words);

        WordleDictionaryLoader loader = new WordleDictionaryLoader();
        WordleDictionary dict = loader.getListWords(testFile.toString());

        List<String> loadedWords = dict.getWords();

        assertEquals(4, loadedWords.size());
        assertTrue(loadedWords.contains("оттек"));
        assertTrue(loadedWords.contains("актер"));
        assertTrue(loadedWords.contains("мытье"));
        assertTrue(loadedWords.contains("абзац"));
    }

    /**
     * тесты состояния
     */

    @Test
    void makeGuess_ValidWord_DecrementsSteps() throws WordNotFoundInDictionaryException, InvalidWordLengthException, GameAlreadyFinishedException {
        WordleGame game = new WordleGame(dictionary, log, "абзац");
        game.makeGuess("аборт");
        game.makeGuess("аванс");
        assertEquals(4, game.getSteps());
    }

    @Test
    void makeGuess_InvalidWord_DoesNotDecrementSteps() {
        WordleGame game = new WordleGame(dictionary, log, "абзац");

        assertThrows(InvalidWordLengthException.class, () -> game.makeGuess("авадакедавра"));
        assertThrows(WordNotFoundInDictionaryException.class, () -> game.makeGuess("шпага"));

        assertEquals(6, game.getSteps());
    }

    @Test
    void makeGuess_CorrectWord_GameEnds() throws WordNotFoundInDictionaryException, InvalidWordLengthException, GameAlreadyFinishedException {
        WordleGame game = new WordleGame(dictionary, log, "абзац");
        game.makeGuess("абзац");
        assertTrue(game.isWin());
    }

    @Test
    void makeGuess_AfterSixWrongAttempts_StepsZeroAndNotWin() throws WordNotFoundInDictionaryException, InvalidWordLengthException, GameAlreadyFinishedException {
        WordleGame game = new WordleGame(dictionary, log, "абзац");
        game.makeGuess("агент");
        game.makeGuess("аборт");
        game.makeGuess("аванс");
        game.makeGuess("авгур");
        game.makeGuess("арбуз");
        game.makeGuess("актер");

        assertEquals(0, game.getSteps());
        assertFalse(game.isWin());
    }

    /**
     * тесты подсказок
     */
    @Test
    void getHint_FirstHint_ReturnsRandomWordFromFullDictionary() throws GameAlreadyFinishedException {
        WordleGame game = new WordleGame(dictionary, log, "абзац");

        String hint = game.getHint();

        assertNotNull(hint);
        assertTrue(dictionary.getWords().contains(hint));
    }

    @Test
    void getHint_MultipleCalls_ReturnsDifferentWords() throws GameAlreadyFinishedException {
        WordleGame game = new WordleGame(dictionary, log, "абзац");

        String hint1 = game.getHint();
        String hint2 = game.getHint();
        String hint3 = game.getHint();

        assertNotEquals(hint1, hint2);
        assertNotEquals(hint2, hint3);
        assertNotEquals(hint1, hint3);
    }

    @Test
    void getHint_AfterGuess_ReturnsWordFromFilteredList() throws WordNotFoundInDictionaryException, GameAlreadyFinishedException, InvalidWordLengthException {
        WordleGame game = new WordleGame(dictionary, log, "абзац");

        game.makeGuess("агент");

        String hint = game.getHint();

        assertNotNull(hint);
        assertTrue(dictionary.getWords().contains(hint));

        assertNotEquals("агент", hint);
    }

    @Test
    void getHint_AfterGuess_HintMatchesMask() throws WordNotFoundInDictionaryException, GameAlreadyFinishedException, InvalidWordLengthException {
        String answer = "абзац";
        WordleGame game = new WordleGame(dictionary, log, answer);

        String word = "агент";

        game.makeGuess(word);
        String hint = game.getHint();

        String hintMask = WordleDictionary.wordComparison(answer, hint);
        String agentMask = WordleDictionary.wordComparison(answer, word);

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (agentMask.charAt(i) == '+') {
                assertEquals('+', hintMask.charAt(i));
            } else if (agentMask.charAt(i) == '^') {
                char requiredChar = word.charAt(i);
                assertTrue(hint.indexOf(requiredChar) != -1);
                assertNotEquals(requiredChar, hint.charAt(i));
            }
        }
    }

    /**
     * тесты игрового цикла
     */

    @Test
    void fullGame_WinScenario() throws WordNotFoundInDictionaryException, GameAlreadyFinishedException, InvalidWordLengthException {
        WordleGame game = new WordleGame(dictionary, log, "абзац");
        game.makeGuess("агент");
        game.makeGuess("арбуз");

        String hint = game.getHint();

        game.makeGuess(hint);

        assertTrue(game.isWin());
        assertTrue(game.isFinished());
    }

    @Test
    void fullGame_LoseScenario() throws WordNotFoundInDictionaryException, GameAlreadyFinishedException, InvalidWordLengthException {
        WordleGame game = new WordleGame(dictionary, log, "абзац");

        game.makeGuess("агент");
        game.makeGuess("аборт");
        game.makeGuess("аванс");
        game.makeGuess("авгур");
        game.makeGuess("арбуз");
        game.makeGuess("актер");

        assertFalse(game.isWin());
        assertTrue(game.isFinished());
        assertEquals(0, game.getSteps());
    }

    @Test
    void fullGameScenario_OnlyHints_ComputerPlaysAlone() throws GameAlreadyFinishedException, WordNotFoundInDictionaryException, InvalidWordLengthException {
        WordleDictionaryLoader loader = new WordleDictionaryLoader();
        WordleDictionary realDict = loader.getListWords("words_ru.txt");

        WordleGame game = new WordleGame(realDict, log);

        int attempts = 0;

        while (!game.isFinished() && attempts < 7) {
            String hint = game.getHint();

            if (hint == null) {
                break;
            }

            game.makeGuess(hint);
            attempts++;
        }
        assertTrue(game.isFinished() && attempts < 7);
    }
}
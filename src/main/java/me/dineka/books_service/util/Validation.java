package me.dineka.books_service.util;

import me.dineka.books_service.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Year;

public class Validation {
    public static final String BOOKTITLE_REGEX = "^[A-Za-zА-Яа-яЁё0-9 -.]+$";
    public static final String BOOKGENRE_REGEX = "^[A-Za-zА-Яа-яЁё -.]+$";
    public static final String AUTHORNAME_REGEX = "^[A-Za-zА-Яа-яЁё -.']+$";
    public static final int CURRENT_YEAR = Year.now().getValue();

    private static final Logger log = LoggerFactory.getLogger(Validation.class);

    /**
     * Валидирует год рождения автора.
     *
     * <p>Год рождения является опциональным (может быть {@code null}).</p>
     * <p>Если год задан, проверяется, что он положительный и что автору не может быть меньше 18 лет
     * на текущий год</p>
     *
     * @param year год рождения автора, может быть {@code null}
     * @throws InvalidAuthorBirthYearException если год рождения отрицательный, нулевой или автору менее 18 лет
     */
    public static void validateBirthYear(Integer year) {
        if (year == null) {     //по тз год рождения автора опционален
            return;
        }
        if (year <= 0 || year > CURRENT_YEAR - 18) {     //исходим из того, автору не может быть меньше 18 лет
            log.error("Некорректный год рождения автора");
            throw new InvalidAuthorBirthYearException("Некорректный год рождения автора: автору не может быть меньше 18 лет");
        }
    }

    /**
     * Валидирует год издания книги
     *
     * <p>Год не может быть {@code null}, отрицательным или больше текущего года.</p>
     *
     * @param year год издания книги
     * @throws InvalidBookPublishingYearException если год {@code null}, отрицательный или больше текущего
     */
    public static void validatePublishingYear(Integer year) {
        if (year == null || year < 0 || year > CURRENT_YEAR) {
            log.error("Некорректный год издания книги");
            throw new InvalidBookPublishingYearException("Некорректный год издания книги");
        }
    }

    /**
     * Валидирует имя автора.
     *
     * <p>Имя не может быть {@code null}, пустым и должно содержать только буквы, пробелы, апострофы, дефисы и точки -
     * в соответствии с регулярным выражением {@code AUTHORNAME_REGEX}.</p>
     *
     * @param string имя автора
     * @throws InvalidAuthorNameException если имя {@code null}, пустое или не соответствует шаблону {@code AUTHORNAME_REGEX}
     */
    public static void validateAuthorName(String string) {
        if (string == null || string.isBlank() || !string.matches(AUTHORNAME_REGEX)) {
            log.error("Некорректное имя автора");
            throw new InvalidAuthorNameException("Некорректное имя автора");
        }
    }

    /**
     * Валидирует название книги.
     *
     * <p>Название не может быть {@code null}, пустым и должно содержать только буквы, цифры, пробелы, точки и дефисы -
     * в соответствии с {@code BOOKTITLE_REGEX}.</p>
     *
     * @param string название книги
     * @throws InvalidBookTitleException если название {@code null}, пустое или не соответствует шаблону {@code BOOKTITLE_REGEX}
     */
    public static void validateBookTitle(String string) {
        if (string == null || string.isBlank() || !string.matches(BOOKTITLE_REGEX)) {
            log.error("Некорректное название книги");
            throw new InvalidBookTitleException("Некорректное название книги");
        }
    }

    /**
     * Валидирует жанр книги.
     *
     * <p>Жанр не может быть {@code null}, пустым и должен содержать только буквы, пробелы, дефисы и точки -
     * в соответствии с {@code BOOKGENRE_REGEX}.</p>
     *
     * @param string жанр книги
     * @throws InvalidBookGenreException если жанр {@code null}, пустой или не соответствует шаблону {@code BOOKGENRE_REGEX}
     */
    public static void validateBookGenre(String string) {
        if (string == null || string.isBlank() || !string.matches(BOOKGENRE_REGEX)) {
            log.error("Некорректный жанр книги");
            throw new InvalidBookGenreException("Некорректный жанр книги");
        }
    }
}
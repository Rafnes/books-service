package me.dineka.books_service.util;

import me.dineka.books_service.exception.InvalidAuthorBirthYearException;
import me.dineka.books_service.exception.InvalidAuthorNameException;
import me.dineka.books_service.exception.InvalidBookPublishingYearException;
import me.dineka.books_service.exception.InvalidBookStringException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Year;

public class Validation {
    public static final String NAME_REGEX = "^[A-Za-zА-Яа-яЁё -.]+$";
    public static final int CURRENT_YEAR = Year.now().getValue();

    private static final Logger log = LoggerFactory.getLogger(Validation.class);

    public static void validateBirthYear(Integer year) {
        if (year == null) {     //по тз год рождения автора опционален
            return;
        }
        if (year <= 0 || year > CURRENT_YEAR - 18) {     //исходим из того, автору не может быть меньше 18 лет
            log.error("Некорректный год рождения автора");
            throw new InvalidAuthorBirthYearException("Некорректный год рождения автора: автору не может быть меньше 18 лет");
        }
    }

    public static void validatePublishingYear(Integer year) {
        if (year == null || year > CURRENT_YEAR) {
            log.error("Некорректный год издания книги");
            throw new InvalidBookPublishingYearException("Некорректный год издания книги");
        }
    }

    public static void validateAuthorName(String string) {
        if (string == null || string.isBlank() || !string.matches(NAME_REGEX)) {
            log.error("Некорректное имя автора");
            throw new InvalidAuthorNameException("Некорректное имя автора");
        }
    }

    public static void validateBookString(String string) {
        if (string == null || string.isBlank() || !string.matches(NAME_REGEX)) {
            log.error("Некорректное название или жанр книги");
            throw new InvalidBookStringException("Некорректное название или жанр книги");
        }
    }
}
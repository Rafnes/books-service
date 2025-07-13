package me.dineka.books_service.util;

import me.dineka.books_service.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationTest {
    String validAuthorName1 = "Андрей Андреев";     //буквы и пробелы
    String validAuthorName2 = "Дж. К. Роулинг";     //буквы, точки и пробелы
    String validAuthorName3 = "С. О'Брайан";    //буквы, точки, пробелы и апострофы
    String validAuthorName4 = "Жан-Жак Руссо";    //буквы, точки, пробелы и дефисы

    String invalidAuthorName1 = "";
    String invalidAuthorName2 = " ";
    String invalidAuthorName3 = "1";
    String invalidAuthorName4 = "1а";
    String invalidAuthorName5 = "61*:%:*%*@а f2";

    String validBookTitle1 = "7";      //цифры
    String validBookTitle2 = "Оно";    //буквы
    String validBookTitle3 = "Жизнь в Сан-Августине";   //буквы, пробелы и дефисы
    String validBookTitle4 = "12 стульев";      //буквы, цифры и пробелы

    String invalidBookTitle1 = "";
    String invalidBookTitle2 = " ";
    String invalidBookTitle3 = "&*4*(@";

    String validBookGenre1 = "Роман";   //буквы
    String validBookGenre2 = "Науч. Поп.";  //буквы, пробелы и точки
    String validBookGenre3 = "Нон - фикшн";   //буквы, пробелы и дефисы

    String invalidBookGenre1 = "";
    String invalidBookGenre2 = " ";
    String invalidBookGenre3 = "11";
    String invalidBookGenre4 = "ff1";
    String invalidBookGenre5 = "&%&51 (0)";

    @Test
    @DisplayName("Положительный тест на проверку года рождения автора, год рождения null")
    void testValidateBirthYear_Positive_1() {
        assertDoesNotThrow(() -> Validation.validateBirthYear(null));
    }

    @Test
    @DisplayName("Положительный тест на проверку года рождения автора, год рождения = сейчас минус 40 лет")
    void testValidateBirthYear_Positive_2() {
        int validBirthYear = LocalDateTime.now().getYear() - 40;
        assertDoesNotThrow(() -> Validation.validateBirthYear(validBirthYear));
    }

    @Test
    @DisplayName("Негативный тест на проверку года рождения автора, год рождения меньше или равен 0")
    void testValidateBirthYear_Negative_1() {
        assertThrows(InvalidAuthorBirthYearException.class, () -> Validation.validateBirthYear(-100));
        assertThrows(InvalidAuthorBirthYearException.class, () -> Validation.validateBirthYear(0));
    }

    @Test
    @DisplayName("Положительный тест на проверку года издания книги, год издания = сейчас - 40 лет")
    void testValidatePublishingYear_Positive() {
        int validPublishingYear = LocalDateTime.now().getYear() - 40;
        assertDoesNotThrow(() -> Validation.validateBirthYear(validPublishingYear));
    }

    @Test
    @DisplayName("Негативный тест на проверку года издания книги, год издания меньше или равен 0")
    void testValidatePublishingYear_Negative_1() {
        assertThrows(InvalidAuthorBirthYearException.class, () -> Validation.validateBirthYear(-100));
        assertThrows(InvalidAuthorBirthYearException.class, () -> Validation.validateBirthYear(0));
    }

    @Test
    @DisplayName("Негативный тест на проверку года издания книги, год издания null")
    void testValidatePublishingYear_Negative_2() {
        assertThrows(InvalidBookPublishingYearException.class, () -> Validation.validatePublishingYear(null));
    }

    @Test
    @DisplayName("Положительный тест на проверку имени писателя")
    void testValidateAuthorName_Positive() {
        assertDoesNotThrow(() -> Validation.validateAuthorName(validAuthorName1));
        assertDoesNotThrow(() -> Validation.validateAuthorName(validAuthorName2));
        assertDoesNotThrow(() -> Validation.validateAuthorName(validAuthorName3));
        assertDoesNotThrow(() -> Validation.validateAuthorName(validAuthorName4));
    }

    @Test
    @DisplayName("Негативный тест на проверку имени писателя")
    void testValidateAuthorName_Negative() {
        assertThrows(InvalidAuthorNameException.class, () -> Validation.validateAuthorName(null));
        assertThrows(InvalidAuthorNameException.class, () -> Validation.validateAuthorName(invalidAuthorName1));
        assertThrows(InvalidAuthorNameException.class, () -> Validation.validateAuthorName(invalidAuthorName2));
        assertThrows(InvalidAuthorNameException.class, () -> Validation.validateAuthorName(invalidAuthorName3));
        assertThrows(InvalidAuthorNameException.class, () -> Validation.validateAuthorName(invalidAuthorName4));
        assertThrows(InvalidAuthorNameException.class, () -> Validation.validateAuthorName(invalidAuthorName5));
    }

    @Test
    @DisplayName("Положительный тест на проверку названия книги")
    void testValidateBookTitle_Positive() {
        assertDoesNotThrow(() -> Validation.validateBookTitle(validBookTitle1));
        assertDoesNotThrow(() -> Validation.validateBookTitle(validBookTitle2));
        assertDoesNotThrow(() -> Validation.validateBookTitle(validBookTitle3));
        assertDoesNotThrow(() -> Validation.validateBookTitle(validBookTitle4));
    }

    @Test
    @DisplayName("Негативный тест на проверку названия книги")
    void testValidateBookTitle_Negative() {
        assertThrows(InvalidBookTitleException.class, () -> Validation.validateBookTitle(null));
        assertThrows(InvalidBookTitleException.class, () -> Validation.validateBookTitle(invalidBookTitle1));
        assertThrows(InvalidBookTitleException.class, () -> Validation.validateBookTitle(invalidBookTitle2));
        assertThrows(InvalidBookTitleException.class, () -> Validation.validateBookTitle(invalidBookTitle3));
    }

    @Test
    @DisplayName("Положительный тест на проверку жанра книги")
    void testValidateBookGenre_Positive() {
        assertDoesNotThrow(() -> Validation.validateBookGenre(validBookGenre1));
        assertDoesNotThrow(() -> Validation.validateBookGenre(validBookGenre2));
        assertDoesNotThrow(() -> Validation.validateBookGenre(validBookGenre3));
    }

    @Test
    @DisplayName("Негативный тест на проверку жанра книги")
    void testValidateBookGenre_Negative() {
        assertThrows(InvalidBookGenreException.class, () -> Validation.validateBookGenre(null));
        assertThrows(InvalidBookGenreException.class, () -> Validation.validateBookGenre(invalidBookGenre1));
        assertThrows(InvalidBookGenreException.class, () -> Validation.validateBookGenre(invalidBookGenre2));
        assertThrows(InvalidBookGenreException.class, () -> Validation.validateBookGenre(invalidBookGenre3));
        assertThrows(InvalidBookGenreException.class, () -> Validation.validateBookGenre(invalidBookGenre4));
        assertThrows(InvalidBookGenreException.class, () -> Validation.validateBookGenre(invalidBookGenre5));
    }
}
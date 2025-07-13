package me.dineka.books_service.service;

import me.dineka.books_service.DTO.CreateAuthorDTO;
import me.dineka.books_service.exception.AuthorAlreadyExistsException;
import me.dineka.books_service.exception.AuthorNotFoundException;
import me.dineka.books_service.exception.InvalidAuthorBirthYearException;
import me.dineka.books_service.exception.InvalidAuthorNameException;
import me.dineka.books_service.model.Author;
import me.dineka.books_service.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static me.dineka.books_service.service.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceTest {
    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Положительный тест на добавление автора")
    void testAddAuthor_Positive() {
        CreateAuthorDTO authorDTO = new CreateAuthorDTO(AUTHOR_NAME_1, BIRTH_YEAR_1);

        Author author = new Author();
        author.setName(AUTHOR_NAME_1);
        author.setBirth_year(BIRTH_YEAR_1);

        when(authorRepository.save(author)).thenReturn(author);

        //test
        Author actual = authorService.addAuthor(authorDTO);

        //check
        verify(authorRepository).save(author);
        assertNotNull(actual);
        assertEquals(AUTHOR_NAME_1, actual.getName());
        assertEquals(BIRTH_YEAR_1, actual.getBirth_year());
    }

    @Test
    @DisplayName("Выбрасывает исключение, если в имени нового автора есть цифры")
    void testAddAuthor_Negative_1() {
        assertThrows(InvalidAuthorNameException.class, () -> authorService.addAuthor(new CreateAuthorDTO("1а", 1990)));
    }

    @Test
    @DisplayName("Выбрасывает исключение, если год рождения нового автора отрицательный")
    void testAddAuthor_Negative_2() {
        assertThrows(InvalidAuthorBirthYearException.class, () -> authorService.addAuthor(new CreateAuthorDTO(AUTHOR_NAME_1, -10)));
    }

    @Test
    @DisplayName("Выбрасывает исключение, если новому автору меньше 18 лет")
    void testAddAuthor_Negative_3() {
        assertThrows(InvalidAuthorBirthYearException.class, () -> authorService.addAuthor(new CreateAuthorDTO(AUTHOR_NAME_1, Year.now().getValue())));
    }

    @Test
    @DisplayName("Выбрасывает исключение, имя нового автора null")
    void testAddAuthor_Negative_4() {
        assertThrows(InvalidAuthorNameException.class, () -> authorService.addAuthor(new CreateAuthorDTO(null, BIRTH_YEAR_1)));
    }

    @Test
    @DisplayName("Выбрасывает исключение, имя нового автора пустое")
    void testAddAuthor_Negative_5() {
        assertThrows(InvalidAuthorNameException.class, () -> authorService.addAuthor(new CreateAuthorDTO(" ", BIRTH_YEAR_1)));
    }

    @Test
    @DisplayName("Выбрасывает исключение, если новый автор уже добавлен")
    void testAddAuthor_Negative_6() {
        CreateAuthorDTO dto = new CreateAuthorDTO(AUTHOR_NAME_1, BIRTH_YEAR_1);

        when(authorRepository.existsByNameIgnoreCaseAndBirthYear(dto.getName(), dto.getBirth_year())).thenReturn(true);

        //test & check
        assertThrows(AuthorAlreadyExistsException.class, () -> authorService.addAuthor(dto));
        verify(authorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Положительный тест на получение существующего автора")
    void testGetAuthor_Positive() {
        Author expected = new Author();
        expected.setName(AUTHOR_NAME_1);
        expected.setBirth_year(BIRTH_YEAR_1);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(expected));

        //test
        Author actual = authorService.getAuthorById(1L);

        //check
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getBirth_year(), actual.getBirth_year());
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Выбрасывает исключение если автор не найден")
    void testGetAuthorNegative_1() {
        assertThrows(AuthorNotFoundException.class, () -> authorService.getAuthorById(100L));
    }


    @Test
    @DisplayName("Положительный тест на получение всех авторов")
    void testGetAllAuthors_Positive() {
        Author author1 = new Author();
        author1.setId(1L);
        author1.setName(AUTHOR_NAME_1);
        author1.setBirth_year(BIRTH_YEAR_1);

        Author author2 = new Author();
        author2.setName(AUTHOR_NAME_2);
        author2.setBirth_year(BIRTH_YEAR_2);

        List<Author> authorList = List.of(author1, author2);

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Author> page = new PageImpl<>(authorList, pageRequest, authorList.size());

        when(authorRepository.findAll(pageRequest)).thenReturn(page);

        //test
        List<Author> actual = authorService.getAllAuthors(pageRequest);

        //check
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(AUTHOR_NAME_1, actual.get(0).getName());
        assertEquals(AUTHOR_NAME_2, actual.get(1).getName());
        verify(authorRepository).findAll(pageRequest);
    }

    @Test
    @DisplayName("Выбрасывает исключение если значение page меньше 0")
    void testGetAllAuthorsNegative_1() {
        assertThrows(IllegalArgumentException.class, () -> authorService.getAllAuthors(PageRequest.of(-5, 5)));
    }


    @Test
    @DisplayName("Выбрасывает исключение если значение size меньше 0")
    void testGetAllAuthorsNegative_2() {
        assertThrows(IllegalArgumentException.class, () -> authorService.getAllAuthors(PageRequest.of(5, -5)));
    }

    @Test
    @DisplayName("Выбрасывает исключение если значение size равно 0")
    void testGetAllAuthorsNegative_3() {
        assertThrows(IllegalArgumentException.class, () -> authorService.getAllAuthors(PageRequest.of(5, 0)));
    }
}
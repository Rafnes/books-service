package me.dineka.books_service.service;

import me.dineka.books_service.DTO.BookResponseDTO;
import me.dineka.books_service.DTO.CreateOrUpdateBookDTO;
import me.dineka.books_service.exception.*;
import me.dineka.books_service.model.Author;
import me.dineka.books_service.model.Book;
import me.dineka.books_service.repository.AuthorRepository;
import me.dineka.books_service.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static me.dineka.books_service.service.TestData.*;
import static org.mockito.Mockito.*;

class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Положительный тест на добавление книги")
    void addBook_Positive() {
        Author author = new Author();
        author.setName(AUTHOR_NAME_1);
        author.setBirth_year(BIRTH_YEAR_1);

        Book book = new Book();
        book.setYear(PUBLISHING_YEAR_1);
        book.setGenre(GENRE_1);
        book.setTitle(BOOK_TITLE_1);
        book.setAuthor(author);

        CreateOrUpdateBookDTO bookDTO = new CreateOrUpdateBookDTO();
        bookDTO.setTitle(BOOK_TITLE_1);
        bookDTO.setGenre(GENRE_1);
        bookDTO.setYear(PUBLISHING_YEAR_1);
        bookDTO.setAuthorId(1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.existsByTitleIgnoreCaseAndYearAndAuthorId(BOOK_TITLE_1, PUBLISHING_YEAR_1, 1L)).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        //test
        Book actual = bookService.addBook(bookDTO);

        //check
        assertNotNull(actual);
        assertEquals(BOOK_TITLE_1, actual.getTitle());
        assertEquals(GENRE_1, actual.getGenre());
        assertEquals(PUBLISHING_YEAR_1, actual.getYear());
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Выбрасывает исключение когда новой книги автора не существует")
    void testAddBook_Negative_1() {
        assertThrows(AuthorNotFoundException.class, () -> bookService.addBook(new CreateOrUpdateBookDTO(BOOK_TITLE_1, GENRE_1, PUBLISHING_YEAR_1, 100L)));
    }

    @Test
    @DisplayName("Выбрасывает исключение когда жанр новой книги некорректен")
    void testAddBook_Negative_2() {
        Author author = new Author();
        author.setId(1L);
        author.setName(AUTHOR_NAME_1);
        author.setBirth_year(BIRTH_YEAR_1);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        assertThrows(InvalidBookGenreException.class, () -> bookService.addBook(new CreateOrUpdateBookDTO(BOOK_TITLE_1, "111", PUBLISHING_YEAR_1, 1L)));
        assertThrows(InvalidBookGenreException.class, () -> bookService.addBook(new CreateOrUpdateBookDTO(BOOK_TITLE_1, null, PUBLISHING_YEAR_1, 1L)));
        assertThrows(InvalidBookGenreException.class, () -> bookService.addBook(new CreateOrUpdateBookDTO(BOOK_TITLE_1, " ", PUBLISHING_YEAR_1, 1L)));
    }

    @Test
    @DisplayName("Выбрасывает исключение когда название новой книги некорректно")
    void testAddBook_Negative_3() {
        Author author = new Author();
        author.setId(1L);
        author.setName(AUTHOR_NAME_1);
        author.setBirth_year(BIRTH_YEAR_1);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        assertThrows(InvalidBookTitleException.class, () -> bookService.addBook(new CreateOrUpdateBookDTO("@", GENRE_1, PUBLISHING_YEAR_1, 1L)));
        assertThrows(InvalidBookTitleException.class, () -> bookService.addBook(new CreateOrUpdateBookDTO(null, GENRE_1, PUBLISHING_YEAR_1, 1L)));
        assertThrows(InvalidBookTitleException.class, () -> bookService.addBook(new CreateOrUpdateBookDTO(" ", GENRE_1, PUBLISHING_YEAR_1, 1L)));
    }

    @Test
    @DisplayName("Выбрасывает исключение когда год издания новой книги некорректен")
    void testAddBook_Negative_4() {
        Author author = new Author();
        author.setId(1L);
        author.setName(AUTHOR_NAME_1);
        author.setBirth_year(BIRTH_YEAR_1);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        assertThrows(InvalidBookPublishingYearException.class, () -> bookService.addBook(new CreateOrUpdateBookDTO(BOOK_TITLE_1, GENRE_1, -4, 1L)));
        assertThrows(InvalidBookPublishingYearException.class, () -> bookService.addBook(new CreateOrUpdateBookDTO(BOOK_TITLE_1, GENRE_1, null, 1L)));
        assertThrows(InvalidBookPublishingYearException.class, () -> bookService.addBook(new CreateOrUpdateBookDTO(BOOK_TITLE_1, GENRE_1, Year.now().getValue() + 10, 1L)));
    }


    @Test
    @DisplayName("Выбрасывает исключение когда новая книга уже существует")
    void testAddBook_Negative_5() {
        Author author = new Author();
        author.setId(1L);
        author.setName(AUTHOR_NAME_1);
        author.setBirth_year(BIRTH_YEAR_1);

        CreateOrUpdateBookDTO bookDTO = new CreateOrUpdateBookDTO();
        bookDTO.setTitle(BOOK_TITLE_1);
        bookDTO.setGenre(GENRE_1);
        bookDTO.setYear(PUBLISHING_YEAR_1);
        bookDTO.setAuthorId(1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.existsByTitleIgnoreCaseAndYearAndAuthorId(BOOK_TITLE_1, PUBLISHING_YEAR_1, 1L)).thenReturn(true);

        //test&check
        assertThrows(BookAlreadyExistsException.class, () -> bookService.addBook(bookDTO));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Положительный тест на получение всех книг")
    void testGetAllBooks() {
        Author author1 = new Author();
        author1.setId(1L);
        author1.setName("Author One");

        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle(BOOK_TITLE_1);
        book1.setGenre(GENRE_1);
        book1.setYear(PUBLISHING_YEAR_1);
        book1.setAuthor(author1);

        Author author2 = new Author();
        author2.setId(2L);
        author2.setName(AUTHOR_NAME_2);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle(BOOK_TITLE_2);
        book2.setGenre(GENRE_2);
        book2.setYear(PUBLISHING_YEAR_2);
        book2.setAuthor(author2);

        List<Book> books = List.of(book1, book2);
        when(bookRepository.findAll()).thenReturn(books);

        //test
        List<BookResponseDTO> result = bookService.getAllBooks();

        //check
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(book1.getId(), result.get(0).getId());
        assertEquals(book1.getTitle(), result.get(0).getTitle());
        assertEquals(book1.getGenre(), result.get(0).getGenre());
        assertEquals(book1.getYear(), result.get(0).getYear());
        assertEquals(book1.getAuthor().getId(), result.get(0).getAuthorId());

        assertEquals(book2.getId(), result.get(1).getId());
        assertEquals(book2.getTitle(), result.get(1).getTitle());
        assertEquals(book2.getGenre(), result.get(1).getGenre());
        assertEquals(book2.getYear(), result.get(1).getYear());
        assertEquals(book2.getAuthor().getId(), result.get(1).getAuthorId());

        verify(bookRepository, times(1)).findAll();
    }
}
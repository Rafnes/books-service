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

import static me.dineka.books_service.service.TestData.*;
import static me.dineka.books_service.util.Validation.CURRENT_YEAR;
import static org.junit.jupiter.api.Assertions.*;
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

        //test & check
        assertThrows(BookAlreadyExistsException.class, () -> bookService.addBook(bookDTO));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Выбрасывает исключение когда год издания новой книги меньше чем год рождения автора")
    void testAddBook_Negative_6() {
        Author author = new Author();
        author.setId(1L);
        author.setName(AUTHOR_NAME_1);
        author.setBirth_year(1900);

        CreateOrUpdateBookDTO bookDTO = new CreateOrUpdateBookDTO();
        bookDTO.setTitle(BOOK_TITLE_1);
        bookDTO.setGenre(GENRE_1);
        bookDTO.setYear(1880);
        bookDTO.setAuthorId(1L);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        // test & check
        assertThrows(InvalidBookPublishingYearException.class, () -> bookService.addBook(bookDTO));
    }

    @Test
    @DisplayName("Положительный тест на получение книги по id")
    void testGetBookById_Positive() {
        Author author = new Author();
        author.setId(1L);
        author.setName(AUTHOR_NAME_1);

        Book book = new Book();
        book.setId(1L);
        book.setTitle(BOOK_TITLE_1);
        book.setGenre(GENRE_1);
        book.setYear(PUBLISHING_YEAR_1);
        book.setAuthor(author);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        //test
        BookResponseDTO actual = bookService.getBookById(1L);

        //check
        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals(book.getTitle(), actual.getTitle());
        assertEquals(book.getGenre(), actual.getGenre());
        assertEquals(book.getYear(), actual.getYear());
        assertEquals(author.getId(), actual.getAuthorId());

        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("Выбрасывает исключение когда книга с заданным id не найдена")
    void testGetBookById_Negative_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));

        verify(bookRepository).findById(1L);
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
        List<BookResponseDTO> actual = bookService.getAllBooks();

        //check
        assertNotNull(actual);
        assertEquals(2, actual.size());

        assertEquals(book1.getId(), actual.get(0).getId());
        assertEquals(book1.getTitle(), actual.get(0).getTitle());
        assertEquals(book1.getGenre(), actual.get(0).getGenre());
        assertEquals(book1.getYear(), actual.get(0).getYear());
        assertEquals(book1.getAuthor().getId(), actual.get(0).getAuthorId());

        assertEquals(book2.getId(), actual.get(1).getId());
        assertEquals(book2.getTitle(), actual.get(1).getTitle());
        assertEquals(book2.getGenre(), actual.get(1).getGenre());
        assertEquals(book2.getYear(), actual.get(1).getYear());
        assertEquals(book2.getAuthor().getId(), actual.get(1).getAuthorId());

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Положительный тест на обновление книги")
    void testUpdateBook_Positive() {
        Author oldAuthor = new Author();
        oldAuthor.setId(1L);
        oldAuthor.setName(AUTHOR_NAME_1);

        Author newAuthor = new Author();
        newAuthor.setId(2L);
        newAuthor.setName(AUTHOR_NAME_2);

        Book oldBook = new Book();
        oldBook.setId(1L);
        oldBook.setTitle(BOOK_TITLE_1);
        oldBook.setGenre(GENRE_1);
        oldBook.setYear(PUBLISHING_YEAR_1);
        oldBook.setAuthor(oldAuthor);

        CreateOrUpdateBookDTO newBookDTO = new CreateOrUpdateBookDTO();
        newBookDTO.setTitle(BOOK_TITLE_2);
        newBookDTO.setGenre(GENRE_2);
        newBookDTO.setYear(CURRENT_YEAR);
        newBookDTO.setAuthorId(2L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(oldBook));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(oldAuthor));
        when(authorRepository.findById(2L)).thenReturn(Optional.of(newAuthor));
        when(bookRepository.existsByTitleIgnoreCaseAndYearAndAuthorId(
                newBookDTO.getTitle(),
                newBookDTO.getYear(),
                newBookDTO.getAuthorId()
        )).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //test
        BookResponseDTO actual = bookService.updateBook(1L, newBookDTO);

        //check
        assertNotNull(actual);
        assertEquals(1L, actual.getId());
        assertEquals(newBookDTO.getTitle(), actual.getTitle());
        assertEquals(newBookDTO.getGenre(), actual.getGenre());
        assertEquals(newBookDTO.getYear(), actual.getYear());
        assertEquals(newBookDTO.getAuthorId(), actual.getAuthorId());
    }

    @Test
    @DisplayName("Выбрасывает исключение когда обновляемой книги не существует")
    void testUpdateBook_Negative_1() {
        assertThrows(BookNotFoundException.class, () -> bookService.updateBook(100L, new CreateOrUpdateBookDTO()));
    }

    @Test
    @DisplayName("Выбрасывает исключение когда не существует автора обновляемой книги")
    void testUpdateBook_Negative_2() {

        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setName(AUTHOR_NAME_1);

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle(BOOK_TITLE_1);
        existingBook.setGenre(GENRE_1);
        existingBook.setYear(PUBLISHING_YEAR_1);
        existingBook.setAuthor(existingAuthor);

        // DTO с несуществующим автором
        CreateOrUpdateBookDTO updatedBookDTO = new CreateOrUpdateBookDTO();
        updatedBookDTO.setTitle(BOOK_TITLE_2);
        updatedBookDTO.setGenre(GENRE_2);
        updatedBookDTO.setYear(2025);
        updatedBookDTO.setAuthorId(100L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(100L)).thenReturn(Optional.empty());

        //test & check
        assertThrows(AuthorNotFoundException.class, () ->
                bookService.updateBook(1L, updatedBookDTO)
        );
    }

    @Test
    @DisplayName("Выбрасывает исключение когда у обновляемой книги некорректное название")
    void testUpdateBook_Negative_3() {
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setName(AUTHOR_NAME_1);

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle(BOOK_TITLE_1);
        existingBook.setGenre(GENRE_1);
        existingBook.setYear(PUBLISHING_YEAR_1);
        existingBook.setAuthor(existingAuthor);

        CreateOrUpdateBookDTO bookDTO = new CreateOrUpdateBookDTO(" ", GENRE_1, PUBLISHING_YEAR_1, 1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));

        //test & check
        assertThrows(InvalidBookTitleException.class, () -> bookService.updateBook(1L, bookDTO));
    }

    @Test
    @DisplayName("Выбрасывает исключение когда у обновляемой книги некорректный жанр")
    void testUpdateBook_Negative_4() {
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setName(AUTHOR_NAME_1);

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle(BOOK_TITLE_1);
        existingBook.setGenre(GENRE_1);
        existingBook.setYear(PUBLISHING_YEAR_1);
        existingBook.setAuthor(existingAuthor);

        CreateOrUpdateBookDTO bookDTO = new CreateOrUpdateBookDTO(BOOK_TITLE_1, "111", PUBLISHING_YEAR_1, 1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));

        //test & check
        assertThrows(InvalidBookGenreException.class, () -> bookService.updateBook(1L, bookDTO));
    }

    @Test
    @DisplayName("Выбрасывает исключение когда у обновляемой книги некорректный год издания")
    void testUpdateBook_Negative_5() {
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setName(AUTHOR_NAME_1);

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle(BOOK_TITLE_1);
        existingBook.setGenre(GENRE_1);
        existingBook.setYear(PUBLISHING_YEAR_1);
        existingBook.setAuthor(existingAuthor);

        CreateOrUpdateBookDTO bookDTO = new CreateOrUpdateBookDTO(BOOK_TITLE_1, GENRE_1, -10, 1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));

        //test & check
        assertThrows(InvalidBookPublishingYearException.class, () -> bookService.updateBook(1L, bookDTO));
    }

    @Test
    @DisplayName("Выбрасывает исключение когда у обновляемой книги год издания раньше года рождения автора")
    void testUpdateBook_Negative_6() {
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setName(AUTHOR_NAME_1);
        existingAuthor.setBirth_year(1900);

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle(BOOK_TITLE_1);
        existingBook.setGenre(GENRE_1);
        existingBook.setYear(2000);
        existingBook.setAuthor(existingAuthor);

        CreateOrUpdateBookDTO bookDTO = new CreateOrUpdateBookDTO(BOOK_TITLE_1, GENRE_1, 1800, 1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));

        //test & check
        assertThrows(InvalidBookPublishingYearException.class, () -> bookService.updateBook(1L, bookDTO));
    }

    @Test
    @DisplayName("Выбрасывает исключение когда у обновляемой книги то же название и имя автора")
    void testUpdateBook_Negative_7() {
        Author existingAuthor = new Author();
        existingAuthor.setId(1L);
        existingAuthor.setName(AUTHOR_NAME_1);
        existingAuthor.setBirth_year(BIRTH_YEAR_1);

        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle(BOOK_TITLE_1);
        existingBook.setGenre(GENRE_1);
        existingBook.setYear(PUBLISHING_YEAR_1);
        existingBook.setAuthor(existingAuthor);

        CreateOrUpdateBookDTO bookDTO = new CreateOrUpdateBookDTO(BOOK_TITLE_1, GENRE_1, PUBLISHING_YEAR_1, 1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existingAuthor));
        when(bookRepository.existsByTitleIgnoreCaseAndYearAndAuthorId(
                BOOK_TITLE_1,
                PUBLISHING_YEAR_1,
                1L
        )).thenReturn(true);

        //test & check
        assertThrows(BookAlreadyExistsException.class, () -> bookService.updateBook(1L, bookDTO));
    }

    @Test
    @DisplayName("Положительный тест на удаление книги")
    void testDeleteBook_Positive() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        // test
        bookService.deleteBook(1L);

        // check
        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Выбрасывает исключение когда книга для удаления не найдена")
    void testDeleteBook_Negative() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));

        verify(bookRepository, never()).deleteById(anyLong());
    }

}
package me.dineka.books_service.service;

import me.dineka.books_service.DTO.BookResponseDTO;
import me.dineka.books_service.DTO.CreateOrUpdateBookDTO;
import me.dineka.books_service.exception.AuthorNotFoundException;
import me.dineka.books_service.exception.BookAlreadyExistsException;
import me.dineka.books_service.exception.BookNotFoundException;
import me.dineka.books_service.exception.InvalidBookPublishingYearException;
import me.dineka.books_service.model.Author;
import me.dineka.books_service.model.Book;
import me.dineka.books_service.repository.AuthorRepository;
import me.dineka.books_service.repository.BookRepository;
import me.dineka.books_service.util.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    Logger log = LoggerFactory.getLogger(BookService.class);

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    /**
     * Добавляет новую книгу.
     *
     * <p>Проверяет, существует ли автор с переданным {@code id}, валидирует данные книги
     * Если все проверки проходят, добавляет книгу в репозиторий</p>
     *
     * @param bookDTO объект {@link CreateOrUpdateBookDTO}, содержащий название книги, жанр, год издания и id автора
     * @return сохраненный объект {@link Book}
     * @throws AuthorNotFoundException если автор с указанным id не найден
     * @throws BookAlreadyExistsException если книга с таким названием, годом издания и автором уже существует
     */
    public Book addBook(CreateOrUpdateBookDTO bookDTO) {
        Long authorId = bookDTO.getAuthorId();
        Author author = authorRepository.findById(authorId).orElseThrow(() -> {
            log.error("Не удалось добавить книгу {}: автора с id {} не существует. Сначала добавьте автора", bookDTO.getTitle(), authorId);
            return new AuthorNotFoundException("Автор с id " + authorId + " не найден");
        });
        validateBook(bookDTO, author);

        if (bookRepository.existsByTitleIgnoreCaseAndYearAndAuthorId(bookDTO.getTitle(), bookDTO.getYear(), bookDTO.getAuthorId())) {
            log.error("Не удалось добавить книгу: книга с таким названием, автором и годом издания уже существует");
            throw new BookAlreadyExistsException("Книга с таким названием, автором и годом издания уже существует");
        }

        Book book = new Book();
        book.setAuthor(author);
        book.setTitle(bookDTO.getTitle());
        book.setGenre(bookDTO.getGenre());
        book.setYear(bookDTO.getYear());
        log.info("Добавляем книгу:{}, автор: {}", book, author.getName());
        return bookRepository.save(book);
    }

    /**
     * Получает список всех книг.
     *
     * <p> Каждая книга преобразуется в {@link BookResponseDTO} для передачи клиенту
     * </p>
     * @return список объектов {@link BookResponseDTO}, представляющих все добавленные книги
     */
    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получает книгу по {@code id}.
     *
     * <p>Если книга с указанным id не найдена, выбрасывается исключение {@link BookNotFoundException}.</p>
     *
     * @param id искомой книги
     * @return объект {@link BookResponseDTO}, представляющий найденную книгу
     * @throws BookNotFoundException если книга с указанным {@code id} не существует
     */
    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> {
            log.error("Книга с id {} не найдена", id);
            return new BookNotFoundException("Книга с id :" + id + " не найдена");
        });
        return BookResponseDTO.fromEntity(book);
    }

    /**
     * Обновляет книгу по {@code bookId}.
     *
     * <p>Метод проверяет, существует ли книга по переданному {@code bookId}, существует ли автор по переданному id
     * из объекта {@code CreateOrUpdateBookDTO}, валидирует данные книги. Проверяет, не существует ли уже книга с переданными
     * названием, жанром, годом издания и автором, и если книги с такими параметрами нет, обновляет поля книги и сохраняет
     * ее в репозитории.
     * </p>
     * @param bookId идентификатор книги для обновления
     * @param updatedBook объект {@link CreateOrUpdateBookDTO}, содержащий новые данные книги
     * @return объект {@link BookResponseDTO} с обновлёнными данными книги
     * @throws BookNotFoundException если книга с указанным {@code bookId} не найдена
     * @throws AuthorNotFoundException если автор с id из {@code updatedBook} не найден
     * @throws BookAlreadyExistsException если существует другая книга с таким же названием, годом издания и автором
     */
    public BookResponseDTO updateBook(Long bookId, CreateOrUpdateBookDTO updatedBook) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> {
            log.error("Не удалось обновить книгу с id {}: книга не найдена", bookId);
            return new BookNotFoundException("Книга с id " + bookId + " не найдена");
        });

        Long authorId = updatedBook.getAuthorId();
        Author author;
        author = authorRepository.findById(authorId).orElseThrow(() -> {
            log.error("Не удалось обновить книгу: автор с id {} не найден", authorId);
            return new AuthorNotFoundException("Автор с id " + authorId + " не найден");
        });

        validateBook(updatedBook, author);

        if (bookRepository.existsByTitleIgnoreCaseAndYearAndAuthorId(updatedBook.getTitle(), updatedBook.getYear(), updatedBook.getAuthorId())) {
            log.error("Не удалось обновить книгу: книга с таким названием, автором и годом издания уже существует");
            throw new BookAlreadyExistsException("Книга с таким названием, автором и годом издания уже существует");
        }

        book.setTitle(updatedBook.getTitle());
        book.setGenre(updatedBook.getGenre());
        book.setYear(updatedBook.getYear());
        book.setAuthor(author);

        bookRepository.save(book);
        log.info("Обновлена книга: {}", book);
        return BookResponseDTO.fromEntity(book);
    }

    /**
     * Удаляет книгу по {@code id}.
     *
     * <p>Если книга с указанным {@code id} не найдена, выбрасывается исключение {@link BookNotFoundException}.</p>
     *
     * @param id идентификатор книги для удаления
     * @throws BookNotFoundException если книга с указанным {@code id} не существует
     */
    public void deleteBook(Long id) {
        log.info("Удаляем книгу с id {}", id);
        if (!bookRepository.existsById(id)) {
            log.error("Не удалось удалить книгу с id {}: книга не найдена", id);
            throw new BookNotFoundException("Книга с id " + id + " не найдена");
        }
        log.info("Удаляем книгу: id: {}", id);
        bookRepository.deleteById(id);
    }

    /**
     * Валидирует данные книги и проверяет корректность года издания относительно года рождения автора.
     *
     * <p>Вызывает методы проверки названия книги, жанра и года издания из класса {@code Validation}
     * Далее метод проверяет, что год издания книги не раньше года рождения автора
     * </p>
     *
     * @param bookDTO объект {@link CreateOrUpdateBookDTO}, содержащий данные книги для валидации
     * @param author объект {@link Author}, связанный с книгой
     * @throws me.dineka.books_service.exception.InvalidBookTitleException если название книги некорректно
     * @throws me.dineka.books_service.exception.InvalidBookGenreException если жанр книги некорректен
     * @throws InvalidBookPublishingYearException если год издания некорректен или раньше года рождения автора
     */
    private void validateBook(CreateOrUpdateBookDTO bookDTO, Author author) {
        Validation.validateBookTitle(bookDTO.getTitle());
        Validation.validateBookGenre(bookDTO.getGenre());
        Validation.validatePublishingYear(bookDTO.getYear());

        if (author.getBirth_year()!= null && bookDTO.getYear() < author.getBirth_year()) {
            log.error("Год издания книги не может быть раньше года рождения автора");
            throw new InvalidBookPublishingYearException("Год издания книги не может быть раньше года рождения автора");
        }
    }
}

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

    public List<BookResponseDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public BookResponseDTO getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> {
            log.error("Книга с id {} не найдена", id);
            return new BookNotFoundException("Книга с id :" + id + " не найдена");
        });
        return BookResponseDTO.fromEntity(book);
    }

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

    public void deleteBook(Long id) {
        log.info("Удаляем книгу с id {}", id);
        if (!bookRepository.existsById(id)) {
            log.error("Не удалось удалить книгу с id {}: книга не найдена", id);
            throw new BookNotFoundException("Книга с id " + id + " не найдена");
        }
        log.info("Удаляем книгу: id: {}", id);
        bookRepository.deleteById(id);
    }

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

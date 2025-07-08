package me.dineka.books_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dineka.books_service.DTO.BookResponseDTO;
import me.dineka.books_service.DTO.CreateOrUpdateBookDTO;
import me.dineka.books_service.model.Book;
import me.dineka.books_service.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@Tag(name = "Книги", description = "Операции для работы с книгами")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Добавить книгу", description = "Добавление новой книги")
    @PostMapping
    public Book addBook(CreateOrUpdateBookDTO bookDTO) {
        return bookService.addBook(bookDTO);
    }

    @Operation(summary = "Получить список всех книг", description = "Получение всех книг")
    @GetMapping
    public List<BookResponseDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @Operation(summary = "Получить книгу", description = "Получение информации о книге")
    @GetMapping("/{id}")
    public BookResponseDTO getBook(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @Operation(summary = "Редактировать книгу", description = "Редактирование книги")
    @PutMapping("/{id}")
    public BookResponseDTO updateBook(@PathVariable Long id, @RequestBody CreateOrUpdateBookDTO updateBookDTO) {
        return bookService.updateBook(id, updateBookDTO);
    }

    @Operation(summary = "Удалить книгу", description = "Удаление книги")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}

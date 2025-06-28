package me.dineka.books_service.controller;

import me.dineka.books_service.DTO.CreateOrUpdateBookDTO;
import me.dineka.books_service.model.Book;
import me.dineka.books_service.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public Book addBook(CreateOrUpdateBookDTO bookDTO) {
        return bookService.addBook(bookDTO);
    }

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody CreateOrUpdateBookDTO updateBookDTO) {
        return bookService.updateBook(id, updateBookDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {

        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}

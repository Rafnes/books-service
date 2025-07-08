package me.dineka.books_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dineka.books_service.DTO.CreateAuthorDTO;
import me.dineka.books_service.model.Author;
import me.dineka.books_service.service.AuthorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@Tag(name = "Авторы", description = "Операции для работы с авторами")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(summary = "Добавить автора", description = "Добавление нового автора")
    @PostMapping
    public Author addAuthor(CreateAuthorDTO dto) {
        return authorService.addAuthor(dto);
    }

    @Operation(summary = "Получить список всех авторов", description = "Получение списка всех авторов с пагинацией")
    @GetMapping
    public List<Author> getAllAuthors(@RequestParam int page,
                                      @RequestParam int size) {
        return authorService.getAllAuthors(PageRequest.of(page, size));
    }

    @Operation(summary = "Получить автора по id", description = "Получение автора по id")
    @GetMapping("/{id}")
    public Author getAuthor(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }
}

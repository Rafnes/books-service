package me.dineka.books_service.controller;

import me.dineka.books_service.DTO.CreateAuthorDTO;
import me.dineka.books_service.model.Author;
import me.dineka.books_service.service.AuthorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public Author addAuthor(CreateAuthorDTO dto) {
        return authorService.addAuthor(dto);
    }

    @GetMapping
    public List<Author> getAllAuthors(@RequestParam int page,
                                      @RequestParam int size) {
        return authorService.getAllAuthors(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Author getAuthor(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }
}

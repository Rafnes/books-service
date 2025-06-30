package me.dineka.books_service.service;

import me.dineka.books_service.DTO.CreateAuthorDTO;
import me.dineka.books_service.exception.AuthorAlreadyExistsException;
import me.dineka.books_service.exception.AuthorNotFoundException;
import me.dineka.books_service.model.Author;
import me.dineka.books_service.repository.AuthorRepository;
import me.dineka.books_service.util.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    Logger log = LoggerFactory.getLogger(AuthorService.class);

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author addAuthor(CreateAuthorDTO dto) {
        validateAuthor(dto);
        Author author = new Author();
        author.setName(dto.getName());
        author.setBirth_year(dto.getBirth_year());
        authorRepository.save(author);
        log.info("Добавлен новый автор: {}", author);
        return author;
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id).orElseThrow(() -> {
            log.warn("Не удалось найти автора с id: {}", id);
            return new AuthorNotFoundException("Автор c id" + id + "не найден");
        });
    }

    public List<Author> getAllAuthors(Pageable request) {
        if (request.getPageNumber() < 0) {
            log.warn("Не удалось получить список авторов: некорректное значение page: {}", request.getPageNumber());
            throw new IllegalArgumentException("Номер страницы не может быть отрицательным");
        }
        if (request.getPageSize() <= 0) {
            log.warn("Не удалось получить список авторов: некорректное значение pageSize: {}", request.getPageSize());
            throw new IllegalArgumentException("Размер страницы не может быть отрицательным");
        }
        return authorRepository.findAll(request).getContent();
    }

    private void validateAuthor(CreateAuthorDTO dto) {
        if (authorRepository.existsByNameIgnoreCaseAndBirth_year(dto.getName(), dto.getBirth_year())) {
            log.error("Не удалось добавить автора: автор {} уже существует", dto.getName());
            throw new AuthorAlreadyExistsException("Не удалось добавить автора: автор " + dto.getName() + " уже существует");
        }
        Validation.validateAuthorName(dto.getName());
        Validation.validateBirthYear(dto.getBirth_year());
    }

}

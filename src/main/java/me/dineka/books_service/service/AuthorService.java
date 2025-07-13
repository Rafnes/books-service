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


    /**
     * Добавляет нового автора
     * @param authorDTO {@link CreateAuthorDTO} для создания автора с полями name и birth_year
     * @return {@link Author}
     */
    public Author addAuthor(CreateAuthorDTO authorDTO) {
        validateAuthor(authorDTO);
        Author author = new Author();
        author.setName(authorDTO.getName());
        author.setBirth_year(authorDTO.getBirth_year());
        authorRepository.save(author);
        log.info("Добавлен новый автор: {}", author.getName());
        return author;
    }

    /**
     * Получает автора по {@code id}
     * @param id id искомого автора
     *
     * @throws AuthorNotFoundException если автор не найден
     * @return {@link Author}
     */
    public Author getAuthorById(Long id) {
        return authorRepository.findById(id).orElseThrow(() -> {
            log.warn("Не удалось найти автора с id: {}", id);
            return new AuthorNotFoundException("Автор c id" + id + "не найден");
        });
    }

    /**
     * Возвращает список авторов с учётом параметров пагинации.
     *
     * @param request объект {@link Pageable}, содержащий параметры страницы и размера страницы
     * @return список {@link Author} из указанной страницы
     * @throws IllegalArgumentException если номер страницы меньше 0 или размер страницы меньше или равен 0
     */
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

    /**
     * Валидирует данные автора перед добавлением в базу.
     * Проверяет, существует ли уже автор с таким именем и годом рождения,
     * а также корректность имени и года рождения.
     *
     * @param authorDTO объект {@link CreateAuthorDTO} с данными автора для валидации
     * @throws AuthorAlreadyExistsException если автор с таким именем и годом рождения уже существует
     * @throws IllegalArgumentException если имя автора или год рождения не проходят валидацию
     */
    private void validateAuthor(CreateAuthorDTO authorDTO) {
        if (authorRepository.existsByNameIgnoreCaseAndBirthYear(authorDTO.getName(), authorDTO.getBirth_year())) {
            log.error("Не удалось добавить автора: автор {} уже существует", authorDTO.getName());
            throw new AuthorAlreadyExistsException("Не удалось добавить автора: автор " + authorDTO.getName() + " уже существует");
        }
        Validation.validateAuthorName(authorDTO.getName());
        Validation.validateBirthYear(authorDTO.getBirth_year());
    }

}

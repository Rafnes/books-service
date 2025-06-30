package me.dineka.books_service.DTO;

import me.dineka.books_service.model.Book;

import java.util.List;

public class BookResponseDTO {
    private Long id;
    private String title;
    private Long authorId;
    private String genre;
    private Integer year;

    public BookResponseDTO() {}

    public BookResponseDTO(Long id, String title, Long authorId, String genre, Integer year) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.genre = genre;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public static BookResponseDTO fromEntity(Book book) {
        return new BookResponseDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getGenre(),
                book.getYear()
        );
    }
}

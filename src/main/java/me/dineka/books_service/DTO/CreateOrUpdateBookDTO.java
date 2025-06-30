package me.dineka.books_service.DTO;

import java.util.Objects;

public class CreateOrUpdateBookDTO {
    private String title;
    private String genre;
    private Integer year;
    private Long authorId;

    public CreateOrUpdateBookDTO() {}

    public CreateOrUpdateBookDTO(String title, String genre, Integer year, Long authorId) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
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

    public Long getAuthorId() {
        return authorId;
    }
    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreateOrUpdateBookDTO that = (CreateOrUpdateBookDTO) o;
        return Objects.equals(title, that.title) && Objects.equals(genre, that.genre) && Objects.equals(year, that.year) && Objects.equals(authorId, that.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, genre, year, authorId);
    }

    @Override
    public String toString() {
        return "CreateOrUpdateBookDTO{" +
                "title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", year=" + year +
                ", authorId=" + authorId +
                '}';
    }
}

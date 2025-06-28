package me.dineka.books_service.repository;

import me.dineka.books_service.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByTitleAndYearAndAuthorId(String title, int year, Long authorId);
}

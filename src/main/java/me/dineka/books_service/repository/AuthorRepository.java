package me.dineka.books_service.repository;

import me.dineka.books_service.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    boolean existsByNameIgnoreCaseAndBirthYear(String name, Integer birthYear);
}

package xyz.bookself.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.bookself.books.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, String> {
}

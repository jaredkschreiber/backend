package xyz.bookself.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.bookself.books.domain.Author;

import java.util.Collection;

public interface AuthorRepository extends JpaRepository<Author, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM authors ORDER BY random() LIMIT ?1")
    Collection<Author> findAnyAuthors(int limit);
}

package xyz.bookself.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.bookself.books.domain.Book;

import java.util.Collection;

public interface BookRepository extends JpaRepository<Book, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM books ORDER BY random() LIMIT ?1")
    Collection<Book> findAnyBooks(int limit);

    @Query(nativeQuery = true, value = "SELECT genre FROM (SELECT DISTINCT genre FROM genres) AS genre ORDER BY random() LIMIT ?1")
    Collection<String> findAnyGenres(int limit);

    @Query(nativeQuery = true, value = "SELECT * FROM books WHERE id IN (SELECT book_id FROM books_authors WHERE author_id=?1) LIMIT ?2")
    Collection<Book> findAllByAuthor(String authorId, int limit);

    @Query(nativeQuery = true, value = "SELECT * FROM books WHERE id IN (SELECT book_id FROM genres WHERE LOWER(genre) LIKE LOWER(?1)) LIMIT ?2")
    Collection<Book> findAllByGenre(String genre, int limit);
}

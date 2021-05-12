package xyz.bookself.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.domain.BookRank;

import java.util.Collection;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM books ORDER BY random() LIMIT ?1")
    Collection<Book> findAnyBooks(int limit);

    @Query(nativeQuery = true, value = "SELECT genre FROM (SELECT DISTINCT genre FROM genres) AS genre ORDER BY random() LIMIT ?1")
    Collection<String> findAnyGenres(int limit);

    @Query(nativeQuery = true, value = "SELECT * FROM books WHERE id IN (SELECT book_id FROM books_authors WHERE author_id=?1) LIMIT ?2")
    Collection<Book> findAllByAuthor(String authorId, int limit);

    @Query(nativeQuery = true, value = "SELECT * FROM books WHERE id IN (SELECT book_id FROM genres WHERE LOWER(genre) LIKE LOWER(?1)) LIMIT ?2")
    Collection<Book> findAllByGenre(String genre, int limit);

    // TODO we're interpreting their query as 'plain', aka not parsed nor stemmed, but the blurb field is stemmed (so we're probably missing lots of matches)
    // TODO Christian: I think I need to rewrite this slightly
    @Query(nativeQuery = true, value = "SELECT MAX(ts_rank(search, plainto_tsquery('simple', ?1))) AS rank, id FROM search_index GROUP BY id ORDER BY rank DESC LIMIT ?2")
    List<BookRank> findBooksByQuery(String query, int limit);
}

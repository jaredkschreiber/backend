package xyz.bookself.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xyz.bookself.books.domain.Popularity;

import javax.transaction.Transactional;
import java.util.Collection;

public interface PopularityRepository extends JpaRepository<Popularity, Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM popular_books ORDER BY rank LIMIT ?1")
    Collection<Popularity> getPopularBooks(int limit);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "DELETE FROM popular_books WHERE 1 = 1")
    void clean();
}

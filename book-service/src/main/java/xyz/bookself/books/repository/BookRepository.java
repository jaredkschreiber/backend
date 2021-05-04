package xyz.bookself.books.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.bookself.books.domain.Book;

public interface BookRepository extends JpaRepository<Book, String> {
}

package xyz.bookself.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.bookself.users.domain.BookList;

public interface BookListRepository extends JpaRepository<BookList, String> {
}


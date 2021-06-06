package xyz.bookself.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xyz.bookself.users.domain.BookList;
import java.util.Collection;

public interface BookListRepository extends JpaRepository<BookList, String> {

    @Query(nativeQuery = true, value = "SELECT book_in_list FROM books_in_list WHERE list_id=?1 LIMIT ?2")
    Collection<String> findAllBookIdInList(String bookListId, int limit);

    @Query(nativeQuery = true, value = "SELECT * FROM book_lists WHERE user_id=?1")
    Collection<BookList> findUserBookLists(Integer userId);

    @Query(nativeQuery = true, value = "SELECT book_in_list FROM books_in_list WHERE list_id=(SELECT id FROM book_lists WHERE user_id=?1 AND list_type='READ')")
    Collection<String> findAllBooksInUserReadBookList(Integer userId);

}
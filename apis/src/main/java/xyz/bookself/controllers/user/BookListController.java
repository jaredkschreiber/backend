package xyz.bookself.controllers.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.bookself.books.domain.Book;
import xyz.bookself.config.BookselfApiConfiguration;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.domain.BookListEnum;
import xyz.bookself.users.repository.BookListRepository;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v1/book-lists")
@Slf4j
public class BookListController {
    private final BookselfApiConfiguration apiConfiguration;
    private final BookListRepository bookListRepository;
    @Autowired
    public BookListController(BookselfApiConfiguration configuration, BookListRepository repository) {
        this.apiConfiguration = configuration;
        this.bookListRepository = repository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookList> getBookList(@PathVariable String id) {
        final BookList booklist = bookListRepository.findById(id).orElseThrow();
        return new ResponseEntity<>(booklist, HttpStatus.OK);
    }

    @GetMapping("/get-books-in-list")
    public ResponseEntity<Collection<String>>getAllBooksInList(@RequestParam String bookListId)
    {
        final Collection<String>booksInList = bookListRepository.findAllBookIdInList(bookListId,apiConfiguration.getMaxReturnedBooks());
        return new ResponseEntity<>(booksInList, HttpStatus.OK);
    }
    @GetMapping("/get-user-book-lists")
    public ResponseEntity<Collection<BookList>>getUserBookList(@RequestParam Integer userId)
    {
        final Collection<BookList> userBookListId = bookListRepository.findUserBookLists(userId);

        return new ResponseEntity<>(userBookListId, HttpStatus.OK);
    }

    @PostMapping(value = "/add-book-to-list", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BookList> addBookToList(@RequestBody BookIdListIdDTO bookIdListIdDTO) {
        final BookList foundBookList = bookListRepository.findById(bookIdListIdDTO.getListId()).orElseThrow();
        final Set<String> booksInList = foundBookList.getBooks();
        booksInList.add(bookIdListIdDTO.getBookId());
        foundBookList.setBooks(booksInList);

        bookListRepository.save(foundBookList);
        return new ResponseEntity<>(foundBookList, HttpStatus.OK);
    }
}

class UserIdDTO{
    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
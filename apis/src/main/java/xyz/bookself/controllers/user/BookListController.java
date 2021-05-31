package xyz.bookself.controllers.user;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.config.BookselfApiConfiguration;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.repository.BookListRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/v1/book-lists")
@Slf4j
@Timed
public class BookListController {

    private final BookselfApiConfiguration apiConfiguration;
    private final BookListRepository bookListRepository;
    private final BookRepository bookRepository;

    @Autowired
    public BookListController(BookselfApiConfiguration configuration, BookListRepository repository, BookRepository bookRepository) {
        this.apiConfiguration = configuration;
        this.bookListRepository = repository;
        this.bookRepository = bookRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookList> getBookList(@PathVariable String id) {
        final BookList booklist = bookListRepository.findById(id).orElseThrow();
        return new ResponseEntity<>(booklist, HttpStatus.OK);
    }

    /**
     * Get books in a book list (shelf)
     * @param bookListId id of shelf
     * @return list of books in shelf
     */
    @GetMapping("/{id}/books")
    public ResponseEntity<Collection<Book>> getBooks(@PathVariable("id") String bookListId) {

        final Collection<String>bookIdsInList = bookListRepository.findAllBookIdInList(bookListId, apiConfiguration.getMaxReturnedBooks());
        final Collection<Book> booksInList = new ArrayList<>();
        
        for(String bookId : bookIdsInList) {
            booksInList.add(bookRepository.findById(bookId).orElseThrow());
        }
        
        return new ResponseEntity<>(booksInList, HttpStatus.OK);
    }

    /**
     * Update a book list (shelf).
     *  - Rename shelf
     *  - Add books
     *  - Remove books
     * Check if fields are not null before updating shelf.
     * Unrecognized fields are ignored.
     * @param shelfDto JSON of the format:
     *      {
     *          "newListName": "New Name",
     *          "booksToBeAdded": [ "book-id-1", "book-id-2" ],
     *          "booksToBeRemoved": [ "book-id-3", "book-id-4" ]
     *      }
     * @param id the shelf id
     * The correct request method for updating is PUT
     */
    @PutMapping(value = "/{id}/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookList> renameShelf(@PathVariable String id, @RequestBody ShelfDto shelfDto) {

        final String newListName = shelfDto.getNewListName();
        final Set<String> booksToBeAdded = shelfDto.getBooksToBeAdded();
        final Set<String> booksToBeRemoved = shelfDto.getBooksToBeRemoved();
        final String newBookListId = shelfDto.getNewBookListId();


        final BookList shelf = bookListRepository.findById(id).orElseThrow();
        final BookList addShelf = bookListRepository.findById(newBookListId).orElseThrow();
        boolean updated = false;

        if(Objects.nonNull(newListName)) {
            shelf.setBookListName(newListName);
            updated = true;
        }

        if(Objects.nonNull(booksToBeAdded) && !booksToBeAdded.isEmpty()) {
            final Set<String> booksInList = addShelf.getBooks();
            booksInList.addAll(booksToBeAdded);
            bookListRepository.save(addShelf);
            updated = true;
        }

        if(Objects.nonNull(booksToBeRemoved) && !booksToBeRemoved.isEmpty()) {
            final Set<String> booksInList = shelf.getBooks();
            booksInList.removeAll(booksToBeRemoved);
            updated = true;
        }

        return new ResponseEntity<>((updated) ? bookListRepository.save(shelf) : shelf, HttpStatus.OK);
    }
}

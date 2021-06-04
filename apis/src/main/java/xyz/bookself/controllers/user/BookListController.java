package xyz.bookself.controllers.user;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.config.BookselfApiConfiguration;
import xyz.bookself.exceptions.BadRequestException;
import xyz.bookself.exceptions.ForbiddenException;
import xyz.bookself.exceptions.UnauthorizedException;
import xyz.bookself.security.BookselfUserDetails;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.repository.BookListRepository;
import xyz.bookself.users.repository.UserRepository;

import javax.validation.Valid;
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
    private final UserRepository userRepository;

    @Autowired
    public BookListController(
        BookselfApiConfiguration configuration,
        BookListRepository repository,
        BookRepository bookRepository,
        UserRepository userRepository
    ) {
        this.apiConfiguration = configuration;
        this.bookListRepository = repository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
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

    @PutMapping(value = "/{id}/update-list-name", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookList> renameShelf(@PathVariable String id, @Valid @RequestBody ShelfNameDTO shelfNameDTO) {

        final String newListName = shelfNameDTO.getNewListName();

        final BookList shelf = bookListRepository.findById(id).orElseThrow();
        boolean updated = false;

        if(Objects.nonNull(newListName)) {
            shelf.setBookListName(newListName);
            updated = true;
        }

        return new ResponseEntity<>((updated) ? bookListRepository.save(shelf) : shelf, HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/move-books", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookList> updateShelf(@PathVariable String id, @RequestBody MoveShelfDTO moveShelfDto) {


        final Set<String> booksToBeAdded = moveShelfDto.getBooksToBeAdded();
        final Set<String> booksToBeRemoved = moveShelfDto.getBooksToBeRemoved();
        final String newBookListId = moveShelfDto.getNewBookListId();

        final BookList shelf = bookListRepository.findById(id).orElseThrow();
        final BookList addShelf = bookListRepository.findById(newBookListId).orElseThrow();
        boolean updated = false;

        if(Objects.nonNull(booksToBeAdded) && !booksToBeAdded.isEmpty()){
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

    @PutMapping(value ="/{id}" , consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookList> updateBookListBookList(@PathVariable String id,  @RequestBody ShelfDto shelfDto) {
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
        if(Objects.nonNull(booksToBeAdded) && !booksToBeAdded.isEmpty()){
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


    // TODO Christian - (opinion) a lot of the above logic should be split up into simpler GET/POST/PUT/PATCH/DELETEs of resources and subresources
    // e.g., instead of having a non-RESTful 'move books' controller method, just DELETE the book from the old list, then POST it to the new list
    // REASON: I see the fetches on the front-end and have no idea what they're doing and it's not so clear what the arguments are.
    //         Also reading some of the above logic is awkward, since it's not really standard behavior.
    // here is what I mean:
    // (much easier to read & debug imo)

    @DeleteMapping("/{bookListId}/books/{bookId}")
    public ResponseEntity<Void> deleteBookFromBookList(
        @PathVariable("bookListId") String bookListId,
        @PathVariable("bookId") String bookId,
        @AuthenticationPrincipal BookselfUserDetails userDetails
    ) {
        // Make sure the user is authenticated and known
        throwIfUserDoesNotExist(userDetails);

        // Make sure it's a known book list
        var bookList = bookListRepository.findById(bookListId).orElseThrow(BadRequestException::new);

        // Make sure it's a known book
        var book = bookRepository.findById(bookId).orElseThrow(BadRequestException::new);

        // Make sure it's their list they are modifying
        if (!userDetails.getId().equals(bookList.getUserId()))
            throw new ForbiddenException();

        // Make sure the book is in the list
        var books = bookList.getBooks();
        if (!books.remove(book.getId()))
            throw new BadRequestException();

        bookListRepository.save(bookList);
        return ResponseEntity.ok().build();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{bookListId}/books/{bookId}")
    public BookList addBookToBookList(
        @PathVariable("bookListId") String bookListId,
        @PathVariable("bookId") String bookId,
        @AuthenticationPrincipal BookselfUserDetails userDetails
    ) {
        // Make sure the user is authenticated and known
        throwIfUserDoesNotExist(userDetails);

        // Make sure it's a known book list
        var bookList = bookListRepository.findById(bookListId).orElseThrow(BadRequestException::new);

        // Make sure it's a known book
        var book = bookRepository.findById(bookId).orElseThrow(BadRequestException::new);

        // Make sure it's their list they are modifying
        if (!userDetails.getId().equals(bookList.getUserId()))
            throw new ForbiddenException();

        // Make sure the book is not already in the list
        var books = bookList.getBooks();
        if (books.contains(book.getId()))
            throw new BadRequestException();

        bookList.getBooks().add(book.getId());
        return bookListRepository.save(bookList);
    }


    private void throwIfUserDoesNotExist(@AuthenticationPrincipal BookselfUserDetails userDetails) {
        if (userDetails == null || !userRepository.existsById(userDetails.getId())) {
            throw new UnauthorizedException();
        }
    }
}

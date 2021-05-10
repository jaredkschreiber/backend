package xyz.bookself.controllers.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.config.BookselfApiConfiguration;

import java.util.Collection;

@RestController
@RequestMapping("/v1/books")
@Slf4j
public class BookController {

    private final BookselfApiConfiguration apiConfiguration;
    private final BookRepository bookRepository;

    @Autowired
    public BookController(BookselfApiConfiguration configuration, BookRepository repository) {
        this.apiConfiguration = configuration;
        this.bookRepository = repository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable String id) {
        final Book book = bookRepository.findById(id).orElseThrow();
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @GetMapping("/by-author")
    public ResponseEntity<Collection<Book>> getBooksByAuthor(@RequestParam String authorId) {
        final Collection<Book> books = bookRepository.findAllByAuthor(authorId, apiConfiguration.getMaxReturnedBooks());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/by-genre")
    public ResponseEntity<Collection<Book>> getBooksByGenre(@RequestParam String genre) {
        final Collection<Book> books = bookRepository.findAllByGenre(genre, apiConfiguration.getMaxReturnedBooks());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Collection<Book>> getBooks() {
        final Collection<Book> books =  bookRepository.findAnyBooks(apiConfiguration.getMaxReturnedBooks());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }
}

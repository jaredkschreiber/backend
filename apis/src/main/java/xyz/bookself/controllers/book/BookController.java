package xyz.bookself.controllers.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.bookself.books.domain.BookRank;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.config.BookselfApiConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<BookDTO> getBook(@PathVariable String id) {
        final var book = bookRepository.findById(id).orElseThrow();
        return new ResponseEntity<>(new BookDTO(book), HttpStatus.OK);
    }

    @GetMapping("/by-author")
    public ResponseEntity<Collection<BookDTO>> getBooksByAuthor(@RequestParam String authorId) {
        final var books = bookRepository.findAllByAuthor(authorId, apiConfiguration.getMaxReturnedBooks())
                .stream().map(BookDTO::new).collect(Collectors.toSet());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/by-genre")
    public ResponseEntity<Collection<BookDTO>> getBooksByGenre(@RequestParam String genre) {
        final var books = bookRepository.findAllByGenre(genre, apiConfiguration.getMaxReturnedBooks())
                .stream().map(BookDTO::new).collect(Collectors.toSet());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/any")
    public ResponseEntity<Collection<BookDTO>> getBooks() {
        final var books = bookRepository.findAnyBooks(apiConfiguration.getMaxReturnedBooks())
                .stream().map(BookDTO::new).collect(Collectors.toSet());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<BookWithRankDTO> getBooksBySearch(@RequestParam String q) {
        final List<BookRank> books = bookRepository.findBooksByQuery(q, apiConfiguration.getMaxReturnedBooks());
        return books.stream()
            .map(bookRank -> {
                final var book = new BookDTO(bookRepository.findById(bookRank.getId()).orElseThrow());
                final var rank = bookRank.getRank();
                return new BookWithRankDTO(book, rank);
            })
            // would be interesting to extend our filtering logic here; e.g., if we have lots of good matches, we can throw out the bad ones
            .filter(book -> book.rank != 0)
            .collect(Collectors.toList());
    }
}

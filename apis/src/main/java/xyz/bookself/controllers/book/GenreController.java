package xyz.bookself.controllers.book;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.config.BookselfApiConfiguration;

import java.util.Collection;

@RestController
@RequestMapping("/v1/genres")
@Timed
public class GenreController {

    private final BookselfApiConfiguration apiConfiguration;
    private final BookRepository bookRepository;

    @Autowired
    public GenreController(BookselfApiConfiguration configuration, BookRepository repository) {
        this.apiConfiguration = configuration;
        this.bookRepository = repository;
    }

    @GetMapping("/any")
    public ResponseEntity<Collection<String>> getGenres() {
        final Collection<String> genres = bookRepository.findAnyGenres(apiConfiguration.getMaxReturnedGenres());
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }
}

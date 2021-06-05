package xyz.bookself.controllers.book;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.books.repository.GenrePopularityRepository;
import xyz.bookself.config.BookselfApiConfiguration;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/v1/genres")
@Timed
public class GenreController {

    private final BookselfApiConfiguration apiConfiguration;
    private final BookRepository bookRepository;
    private final GenrePopularityRepository genrePopularityRepository;

    @Autowired
    public GenreController(BookselfApiConfiguration configuration,
                           BookRepository bookRepository,
                           GenrePopularityRepository genreRepository) {
        this.apiConfiguration = configuration;
        this.bookRepository = bookRepository;
        this.genrePopularityRepository = genreRepository;
    }

    @GetMapping("")
    public ResponseEntity<Collection<String>> getGenres(@RequestParam Optional<String> popular) {
        final boolean isPopular = "yes".equalsIgnoreCase(popular.orElse(""));
        return isPopular
                ? new ResponseEntity<>(genrePopularityRepository.getPopularGenres(Integer.MAX_VALUE), HttpStatus.OK)
                : new ResponseEntity<>(bookRepository.findAnyGenres(apiConfiguration.getMaxReturnedGenres()), HttpStatus.OK);
    }

    @GetMapping("/any")
    public ResponseEntity<Collection<String>> getGenres() {
        final Collection<String> genres = bookRepository.findAnyGenres(apiConfiguration.getMaxReturnedGenres());
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }
}

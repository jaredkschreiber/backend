package xyz.bookself.controllers.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.bookself.books.repository.GenrePopularityRepository;
import xyz.bookself.books.repository.PopularityRepository;
import xyz.bookself.config.BookselfApiConfiguration;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(PopularityController.POPULAR_ENDPOINT)
@Slf4j
public class PopularityController {
    public static final String POPULAR_ENDPOINT = "/v1/books/populars";

    private final PopularityRepository popularityRepository;
    private final GenrePopularityRepository genrePopularityRepository;
    private final BookselfApiConfiguration apiConfiguration;

    @Autowired
    public PopularityController(PopularityRepository popularityRepository,
                                GenrePopularityRepository genrePopularityRepository,
                                BookselfApiConfiguration configuration) {
        this.popularityRepository = popularityRepository;
        this.genrePopularityRepository = genrePopularityRepository;
        this.apiConfiguration = configuration;
    }

    @GetMapping("")
    public ResponseEntity<Collection<PopularityDTO>> getPopularBooks(@RequestParam(name = "genre") Optional<String> g) {
        final String genre = g.orElse("");
        final int limitGenre = apiConfiguration.getMaxPopularBooksByGenreCount();
        final int limitPopular = apiConfiguration.getMaxPopularBooksCount();
        return "".equals(genre)
                ?
                new ResponseEntity<>(popularityRepository.getPopularBooks(limitPopular).stream()
                        .map(PopularityDTO::new)
                        .collect(Collectors.toList()), HttpStatus.OK)
                :
                new ResponseEntity<>(genrePopularityRepository.getPopularBooksByGenre(genre, limitGenre).stream()
                        .map(PopularityDTO::new)
                        .collect(Collectors.toList()), HttpStatus.OK);
    }
}

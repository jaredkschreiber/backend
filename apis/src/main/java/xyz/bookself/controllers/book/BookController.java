package xyz.bookself.controllers.book;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.bookself.books.domain.BookRank;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.config.BookselfApiConfiguration;
import xyz.bookself.exceptions.NotFoundException;
import xyz.bookself.services.BookService;
import xyz.bookself.services.PopularityService;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BookController.BOOKS_ENDPOINT)
@Slf4j
@Timed
public class BookController {

    public static final String BOOKS_ENDPOINT = "/v1/books";

    private final BookselfApiConfiguration apiConfiguration;
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final PopularityService popularityService;

    @Autowired
    public BookController(BookselfApiConfiguration configuration,
                          BookRepository repository,
                          BookService bookService,
                          PopularityService popularityService) {
        this.apiConfiguration = configuration;
        this.bookRepository = repository;
        this.bookService = bookService;
        this.popularityService = popularityService;
    }

    @GetMapping("")
    public ResponseEntity<Collection<BookDTO>> getBooks(@RequestParam Map<String, String> params) {
        final String popular = params.get("popular");
        final String genre = params.get("genre");
        final String authorId = params.get("authorId");
        if("yes".equalsIgnoreCase(popular)) {
            if(Objects.nonNull(genre)) {
                return new ResponseEntity<>(popularityService.findPopularBooksByGenre(genre), HttpStatus.OK);
            }
            return new ResponseEntity<>(popularityService.findPopularBooks(), HttpStatus.OK);
        }
        if(Objects.nonNull(genre)) {
            return new ResponseEntity<>(bookService.findBooksByGenre(genre), HttpStatus.OK);
        }
        if(Objects.nonNull(authorId)) {
            return new ResponseEntity<>(bookService.findBooksByAuthor(authorId), HttpStatus.OK);
        }
        return new ResponseEntity<>(bookService.findAnyBooks(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable String id) {
        final var book = bookRepository.findById(id).orElseThrow();
        return new ResponseEntity<>(new BookDTO(book), HttpStatus.OK);
    }

    @GetMapping("/by-author")
    public ResponseEntity<Collection<BookDTO>> getBooksByAuthor(@RequestParam String authorId) {
        return new ResponseEntity<>(bookService.findBooksByAuthor(authorId), HttpStatus.OK);
    }

    @GetMapping("/by-genre")
    public ResponseEntity<Collection<BookDTO>> getBooksByGenre(@RequestParam String genre) {
        return new ResponseEntity<>(bookService.findBooksByGenre(genre), HttpStatus.OK);
    }

    @GetMapping("/any")
    public ResponseEntity<Collection<BookDTO>> getAnyBooks() {
        return new ResponseEntity<>(bookService.findAnyBooks(), HttpStatus.OK);
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

    @GetMapping("/search-paginated")
    public ResponseEntity<SearchResultsPage> searchPageable(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "page") @Min(1) Optional<Integer> p) {
        final int page = p.orElse(1) - 1;
        final int resultsPerPage = apiConfiguration.getSearchResultsPerPage();
        final Page<BookRank> results = bookRepository.findBooksByQueryPageable(query, PageRequest.of(page, resultsPerPage));
        final long totalElements = results.getTotalElements();
        final int totalPages = results.getTotalPages();
        if(page > totalPages - 1) {
            throw new NotFoundException();
        }
        log.info("Retrieved Page {} of {} out of a total of {} results.", 1 + page, totalPages, totalElements);
        final List<BookWithRankDTO> rankedBooks = results.stream()
                .map(bookRank -> {
                    final var book = new BookDTO(bookRepository.findById(bookRank.getId()).orElseThrow());
                    final var rank = bookRank.getRank();
                    return new BookWithRankDTO(book, rank);
                })
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        final SearchResultsPage searchResultsPage = new SearchResultsPage(1 + page, totalPages, totalElements, rankedBooks);
        return new ResponseEntity<>(searchResultsPage, HttpStatus.OK);
    }
}

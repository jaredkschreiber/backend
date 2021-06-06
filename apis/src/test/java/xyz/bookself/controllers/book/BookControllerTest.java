package xyz.bookself.controllers.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.domain.BookRank;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.services.BookService;
import xyz.bookself.services.PopularityService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    private final String apiPrefix = "/v1/books";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookService bookService;

    @MockBean
    private PopularityService popularityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${bookself.api.max-returned-books}")
    private int maxReturnedBooks;

    @Value("${bookself.api.max-popular-books-by-genre-count}")
    private int maxPopularBooksByGenreCount;

    @Test
    void givenBookExists_whenIdIsSuppliedToBookEndpoint_thenBookIsReturned()
            throws Exception {
        final String validBookId = "9999999999";
        final Book bookThatExistsInDatabase = new Book();
        bookThatExistsInDatabase.setId(validBookId);
        when(bookRepository.findById(validBookId)).thenReturn(Optional.of(bookThatExistsInDatabase));
        mockMvc.perform(get(apiPrefix + "/" + validBookId))
                .andExpect(status().isOk());
    }

    @Test
    void givenAuthorId_whenGetRequestedOnBooksEndpoint_thenAllBooksWrittenByAuthorAreReturned()
            throws Exception {

        final String validAuthorId = "12345";
        final Author author = new Author();
        author.setId(validAuthorId);
        final Set<BookDTO> books = IntStream.rangeClosed(1, maxReturnedBooks)
                .mapToObj(i -> {
                    final Book b = new Book();
                    b.setId(Integer.toHexString(i));
                    b.setAuthors(new HashSet<>(Collections.singletonList(author)));
                    return b;
                })
                .map(BookDTO::new)
                .collect(Collectors.toSet());
        final String jsonContent = objectMapper.writeValueAsString(books);

        when(bookService.findBooksByAuthor(validAuthorId)).thenReturn(books);

        mockMvc.perform(get(apiPrefix).param( "authorId", validAuthorId))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void givenGenre_whenGetRequestedOnBooksEndpoint_theBooksInThatGenreShouldBeReturned() throws Exception {
        final String genre = "Some Genre";
        final Set<BookDTO> books = IntStream.rangeClosed(1, maxReturnedBooks)
                .mapToObj(i -> {
                    final Book b = new Book();
                    b.setId(Integer.toHexString(i));
                    b.setGenres(Set.of(genre));
                    return b;
                })
                .map(BookDTO::new)
                .collect(Collectors.toSet());
        when(bookService.findBooksByGenre(genre)).thenReturn(books);
        mockMvc.perform(get(apiPrefix).param("genre", genre))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(books)));
    }

    @Test
    void givenThereAreBooks_whenGetRequestedWithoutQueryParams_thenAnyBooksShouldBeReturned() throws Exception {
        final Set<BookDTO> books = IntStream.rangeClosed(1, maxReturnedBooks)
                .mapToObj(i -> {
                    final Book b = new Book();
                    b.setId(Integer.toHexString(i));
                    return b;
                })
                .map(BookDTO::new)
                .collect(Collectors.toSet());
        when(bookService.findAnyBooks()).thenReturn(books);
        mockMvc.perform(get(apiPrefix))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(books)));
    }

    @Test
    void givenThereArePopularBooks_whenGetRequestedForPopularBooks_thenSomePopularsAreReturned()
        throws Exception {
        final Set<BookDTO> books = IntStream.rangeClosed(1, maxReturnedBooks)
                .mapToObj(i -> {
                    final Book b = new Book();
                    b.setId(Integer.toHexString(i));
                    return b;
                })
                .map(BookDTO::new)
                .collect(Collectors.toSet());
        when(popularityService.findPopularBooks()).thenReturn(books);
        mockMvc.perform(get(apiPrefix).param("popular", "yes"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(books)));
    }

    @Test
    void givenGenre_whenGetRequestedOnPopularBooks_thenBooksFromThatGenreAreReturned()
            throws Exception {
        final String genre = "Some Genre";
        final Set<BookDTO> books = IntStream.rangeClosed(1, maxPopularBooksByGenreCount)
                .mapToObj(i -> {
                    final Book b = new Book();
                    b.setId(Integer.toHexString(i));
                    b.setGenres(Set.of(genre));
                    return b;
                })
                .map(BookDTO::new)
                .collect(Collectors.toSet());
        final String jsonContent = objectMapper.writeValueAsString(books);
        when(popularityService.findPopularBooksByGenre(genre)).thenReturn(books);
        mockMvc.perform(get(apiPrefix).param("popular", "yes").param("genre", genre))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void givenAuthorId_whenGetRequestedOnBookList_thenAllBooksWrittenByAuthorAreReturned()
            throws Exception {

        final String validAuthorId = "12345";
        final Author author = new Author();
        author.setId(validAuthorId);
        final Set<BookDTO> books = IntStream.rangeClosed(1, maxReturnedBooks)
                .mapToObj(i -> {
                    final Book b = new Book();
                    b.setId(Integer.toHexString(i));
                    b.setAuthors(new HashSet<>(Collections.singletonList(author)));
                    return b;
                })
                .map(BookDTO::new)
                .collect(Collectors.toSet());
        final String jsonContent = objectMapper.writeValueAsString(books);

        when(bookService.findBooksByAuthor(validAuthorId)).thenReturn(books);

        mockMvc.perform(get(apiPrefix + "/by-author?authorId=" + validAuthorId))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void givenThereAreEnoughBooks_whenGetRequestedToBooksAll_thenNBooksShouldBeReturned()
            throws Exception {

        final Collection<BookDTO> sixtyBooks = IntStream.rangeClosed(1, maxReturnedBooks)
                .mapToObj(i -> {
                    Book b = new Book();
                    b.setId("_" + i);
                    return b;
                })
                .map(BookDTO::new)
                .collect(Collectors.toSet());
        final String jsonContent = objectMapper.writeValueAsString(sixtyBooks);

        when(bookService.findAnyBooks()).thenReturn(sixtyBooks);

        mockMvc.perform(get(apiPrefix + "/any"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void givenThereAreEnoughBooks_whenGetRequestedToBooksSearch_thenNBooksShouldBeReturned() throws Exception {
        final List<BookRank> sixtyBooksAsBookRank = IntStream.range(0, maxReturnedBooks)
            .mapToObj(i -> new BookRank() {
                @Override
                public Double getRank() {
                    return ThreadLocalRandom.current().nextInt(0, 2) == 0 ?  0.0 : 1.0;
                }
                @Override
                public String getId() { return "1"; }
            })
            .collect(Collectors.toList());

        String query = "Hello";
        when(bookRepository.findBooksByQuery(query, maxReturnedBooks)).thenReturn(sixtyBooksAsBookRank);
        when(bookRepository.findById("1")).thenReturn(Optional.of(new Book()));

        mockMvc.perform(get(apiPrefix + "/search?q=" + query))
            .andExpect(status().isOk());
    }

    @Test
    void testBookDTOConstructorToIncreaseCoverage() {
        final BookDTO bookDTO = new BookDTO("id",
                "title",
                Collections.emptySet(),
                Collections.emptySet(),
                "blurb",
                0,
                LocalDate.now(),
                Collections.emptySet(),
                5.0);
        assertThat(bookDTO).isNotNull();
    }
}

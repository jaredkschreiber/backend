package xyz.bookself.controllers.user;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.books.repository.RatingRepository;

import xyz.bookself.security.WithBookselfUserDetails;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.repository.BookListRepository;
import xyz.bookself.users.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RecommendationEngineTest {

    private static final int authenticatedUserId = 1;
    private static final int unauthorizedUser = 0;
    private final String apiPrefix = "/v1/recommendations";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private RatingRepository ratingRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private BookListRepository bookListRepository;

    @Test
    void whenUnauthorizedUserGoesForRecommendationByAuthorOrGenre_thenStatusIsUnauthorized()
            throws Exception
    {
        final String validBookListId = "99";
        final BookList bookListThatExistsInDatabase = new BookList();
        bookListThatExistsInDatabase.setId(validBookListId);
        mockMvc.perform(get(apiPrefix + "/" + unauthorizedUser).param("recommend-by", "author"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get(apiPrefix + "/" + unauthorizedUser).param("recommend-by", "genre"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void whenAuthorizedUserGoesForRecommendationByAuthor_thenReturnABookBySaidAuthor()
            throws Exception
    {
        final String validBookListId = "99";
        final Set<String> setOfBooks = new HashSet<>(Arrays.asList("book-id-1"));
        final Author author = new Author();
        author.setId("author_id_1");
        final Set<Author> bookAuthorSet = new HashSet<>();
        bookAuthorSet.add(author);
        final BookList bookListThatExistsInDatabase = new BookList();
        bookListThatExistsInDatabase.setId(validBookListId);

        final Book foundBook = new Book();
        foundBook.setAuthors(bookAuthorSet);

        final Collection<Book> booksBySameAuthor = IntStream.range(0, 1).mapToObj(i -> {
            Book newBook = new Book();
            newBook.setAuthors(bookAuthorSet);
            return newBook;
        }).collect(Collectors.toSet());


        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookListRepository.findAllBooksInUserReadBookList(authenticatedUserId)).thenReturn(setOfBooks);
        when(bookRepository.findById("book-id-1")).thenReturn(Optional.of(foundBook));
        when(bookRepository.findAllByAuthor("author_1", 5)).thenReturn(booksBySameAuthor);


        mockMvc.perform(get(apiPrefix + "/" + authenticatedUserId).param("recommend-by", "author"))
                .andExpect(status().isOk());

    }

}

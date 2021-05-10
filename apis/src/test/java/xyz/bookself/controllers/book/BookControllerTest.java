package xyz.bookself.controllers.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.repository.BookRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Value("${bookself.api.max-returned-books}")
    private int maxReturnedBooks;

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
    void givenAuthorId_whenGetRequestedOnBookList_thenAllBooksWrittenByAuthorAreReturned()
            throws Exception {

        final String validAuthorId = "12345";
        final Author author = new Author();
        author.setId(validAuthorId);
        final Set<Book> books = IntStream.range(100, 110)
                .mapToObj(i -> {
                    final Book b = new Book();
                    b.setId(Integer.toHexString(i));
                    b.setAuthors(new HashSet<>(Collections.singletonList(author)));
                    return b;
                }).collect(Collectors.toSet());
        final String jsonContent = TestUtilities.toJsonString(books);

        when(bookRepository.findAllByAuthor(validAuthorId, maxReturnedBooks)).thenReturn(books);

        mockMvc.perform(get(apiPrefix + "/by-author?authorId=" + validAuthorId))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void givenThereAreEnoughBooks_whenGetRequestedToBooksAll_thenNBooksShouldBeReturned()
            throws Exception {

        final Collection<Book> sixtyBooks = IntStream.range(0, maxReturnedBooks).mapToObj(i -> {
            Book b = new Book();
            b.setId("_" + i);
            return b;
        }).collect(Collectors.toSet());

        when(bookRepository.findAnyBooks(maxReturnedBooks)).thenReturn(sixtyBooks);

        mockMvc.perform(get(apiPrefix + "/any"))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtilities.toJsonString(sixtyBooks)));
    }
}

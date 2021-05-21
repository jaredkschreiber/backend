package xyz.bookself.controllers.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.repository.AuthorRepository;
import xyz.bookself.controllers.TestUtilities;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorControllerTest {

    private final String apiPrefix = "/v1/authors";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorRepository authorRepository;

    @Value("${bookself.api.max-returned-authors}")
    private int maxReturnedAuthors;


    @Test
    void givenAuthorExists_whenGetRequestedWithIdOnPath_thenAuthorShouldBeReturned()
            throws Exception {

        final String existingAuthorId = "12345";
        final Author author = new Author();
        author.setId(existingAuthorId);
        final String jsonContent = TestUtilities.toJsonString(author);

        when(authorRepository.findById(existingAuthorId)).thenReturn(Optional.of(author));

        mockMvc.perform(get(apiPrefix + "/" + existingAuthorId))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void givenThereAreEnoughAuthors_whenGetRequestedToAuthorsAll_thenNAuthorsShouldBeReturned()
            throws Exception {

        final Collection<Author> authors = IntStream.range(0, maxReturnedAuthors)
                .mapToObj(i -> {
                    Author a = new Author();
                    a.setId("_" + i);
                    return a;
                }).collect(Collectors.toSet());

        when(authorRepository.findAnyAuthors(maxReturnedAuthors)).thenReturn(authors);

        mockMvc.perform(get(apiPrefix + "/any"))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtilities.toJsonString(authors)));
    }
}

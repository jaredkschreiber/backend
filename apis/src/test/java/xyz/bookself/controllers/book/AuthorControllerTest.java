package xyz.bookself.controllers.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.repository.AuthorRepository;

import java.util.Optional;

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
}

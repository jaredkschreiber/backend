package xyz.bookself.controllers.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.controllers.TestUtilities;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GenresControllerTest {

    private final String apiPrefix = "/v1/genres";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @Value("${bookself.api.max-returned-genres}")
    private int maxReturnedGenres;

    @Test
    void givenThereAreEnoughGenres_WhenGetRequestedToGenresAll_thenNGenresShouldBeReturned()
            throws Exception {

        final Collection<String> genres = IntStream.range(0, maxReturnedGenres)
                .mapToObj(Integer::toHexString)
                .collect(Collectors.toSet());

        when(bookRepository.findAnyGenres(maxReturnedGenres)).thenReturn(genres);

        mockMvc.perform(get(apiPrefix + "/any"))
                .andExpect(status().isOk())
                .andExpect(content().json(TestUtilities.toJsonString(genres)));
    }
}

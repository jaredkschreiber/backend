package xyz.bookself.controllers.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.users.domain.BookList;
import xyz.bookself.users.repository.BookListRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookListControllerTest {

    private final String apiPrefix = "/v1/book-lists";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookListRepository bookListRepository;

    @Test
    void givenBookListExists_whenIdIsSuppliedToBookListEndpoint_thenABookListIsReturned()
            throws Exception {
        final String validBookListId = "99999999999";
        final BookList bookListThatExistsInDatabase = new BookList();
        bookListThatExistsInDatabase.setId(validBookListId);

        when(bookListRepository.findById(validBookListId)).thenReturn(Optional.of(bookListThatExistsInDatabase));
        mockMvc.perform(get(apiPrefix + "/" + validBookListId))
                .andExpect(status().isOk());
    }
}

package xyz.bookself.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.repository.BookRepository;

import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @Test
    void givenBookDoesNotExist_whenGetRequestedWithBookId_thenResponseShouldBeNotFound()
            throws Exception {
        final String nonExistentId = "10000000000";
        when(bookRepository.findById(nonExistentId)).thenThrow(NoSuchElementException.class);
        mockMvc.perform(get("/book/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andDo(print())
                .andExpect(content().string(containsString("/book/" + nonExistentId)));
    }
}

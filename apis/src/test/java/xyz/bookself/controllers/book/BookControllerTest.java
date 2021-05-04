package xyz.bookself.controllers.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.repository.BookRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @Test
    public void givenBookExists_whenIdIsSuppliedToBookEndpoint_thenBookIsReturned()
            throws Exception {
        final String validBookId = "9999999999";
        final Book bookThatExistsInDatabase = new Book();
        bookThatExistsInDatabase.setId(validBookId);
        when(bookRepository.findById(validBookId)).thenReturn(Optional.of(bookThatExistsInDatabase));
        mockMvc.perform(get("/book/" + validBookId))
                .andExpect(status().isOk());
    }

    @Test
    public void givenBookDoesNotExist_whenBookIsPosted_thenSaveEndpointReturnsBook()
            throws Exception {

        final Book newBook = new Book();
        final String id = "999999999";
        final int pages = 100;
        newBook.setId(id);
        newBook.setPages(pages);

        when(bookRepository.save(newBook)).thenReturn(newBook);

        final String jsonContent = toJsonString(newBook);

        mockMvc.perform(
                post("/book/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    private String toJsonString(Object o) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(o);
    }
}

package xyz.bookself.controllers.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.domain.GenrePopularity;
import xyz.bookself.books.domain.Popularity;
import xyz.bookself.books.repository.GenrePopularityRepository;
import xyz.bookself.books.repository.PopularityRepository;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PopularityControllerTest {

    private final String endpoint = PopularityController.POPULAR_ENDPOINT;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PopularityRepository popularityRepository;

    @MockBean
    private GenrePopularityRepository genrePopularityRepository;

    @Value("${bookself.api.max-popular-books-count}")
    private int limitPopular;

    @Value("${bookself.api.max-popular-books-by-genre-count}")
    private int limitGenre;

    @Test
    void givenPathWithoutGenre_whenGetRequested_thenAllPopularBooksAreReturned() throws Exception {
        final Book b1 = new Book();
        b1.setId("b1");
        final Book b2 = new Book();
        b2.setId("b2");
        final Popularity p1 = new Popularity(1, b1, 1, LocalDateTime.MIN);
        final Popularity p2 = new Popularity(2, b2, 2, LocalDateTime.MIN);
        final PopularityDTO dto1 = new PopularityDTO(p1);
        final PopularityDTO dto2 = new PopularityDTO(p2);
        when(popularityRepository.getPopularBooks(limitPopular)).thenReturn(Set.of(p1, p2));
        mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Set.of(dto1, dto2))));
    }

    @Test
    void givenPathWithQueryParam_whenGetRequested_thenPopularBooksInGenresAreReturned() throws Exception {
        final Book b1 = new Book();
        b1.setId("b1");
        final Book b2 = new Book();
        b2.setId("b2");
        final String genre = "Some Genre";
        final GenrePopularity gp1 = new GenrePopularity(1, genre, b1, 1, LocalDateTime.MIN);
        final GenrePopularity gp2 = new GenrePopularity(2, genre, b2, 2, LocalDateTime.MIN);
        final PopularityDTO dto1 = new PopularityDTO(gp1);
        final PopularityDTO dto2 = new PopularityDTO(gp2);
        when(genrePopularityRepository.getPopularBooksByGenre(genre, limitGenre)).thenReturn(Set.of(gp1, gp2));
        mockMvc.perform(get(endpoint + "?genre=" + genre))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Set.of(dto1, dto2))));
    }
}

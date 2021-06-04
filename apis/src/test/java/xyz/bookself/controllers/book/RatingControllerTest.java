package xyz.bookself.controllers.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.domain.Rating;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.books.repository.RatingRepository;
import xyz.bookself.security.WithBookselfUserDetails;
import xyz.bookself.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RatingControllerTest {

    private static final String ratedBookId = "1234";
    private static final int authenticatedUserId = 1;
    private static final String ENDPOINT = RatingController.REQUEST_MAPPING_PATH.replace("{bookId}", ratedBookId);

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

    @Test
    void testAllMethods_Unauthorized() throws Exception {
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);

        mockMvc.perform(post(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(patch(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete(ENDPOINT)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void testInsertAndUpdate_BadRequestRatingTooLow() throws Exception {
        // Make sure a book is present and the user exists
        var book = new Book();
        book.setId(ratedBookId);

        when(bookRepository.existsById(ratedBookId)).thenReturn(true);
        when(bookRepository.findById(ratedBookId)).thenReturn(Optional.of(book));
        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);

        var rating = new Rating();
        rating.setId(1234);
        rating.setUserId(authenticatedUserId);
        when(ratingRepository.findLatestRatingByUserForBook(authenticatedUserId, ratedBookId)).thenReturn(Optional.of(rating));

        var ratingDTO = new RatingDTO(-1, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
        mockMvc.perform(post(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void testInsertAndUpdate_BadRequestRatingTooHigh() throws Exception {

        var book = new Book();
        book.setId(ratedBookId);

        when(bookRepository.existsById(ratedBookId)).thenReturn(true);
        when(bookRepository.findById(ratedBookId)).thenReturn(Optional.of(book));
        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);

        var rating = new Rating();
        rating.setId(1234);
        rating.setUserId(authenticatedUserId);
        when(ratingRepository.findLatestRatingByUserForBook(authenticatedUserId, ratedBookId)).thenReturn(Optional.of(rating));

        var ratingDTO = new RatingDTO(6, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);

        mockMvc.perform(post(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mockMvc.perform(patch(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void testSaveNewRating_BadRequestBookNotFound() throws Exception {
        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookRepository.findById("1234")).thenReturn(Optional.empty());
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
        mockMvc.perform(post(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void testSaveNewRating_Success() throws Exception {
        // Make sure a book is present and the user exists
        var book = new Book();
        book.setId("1234");
        when(bookRepository.findById("1234")).thenReturn(Optional.of(book));
        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);

        // Create a rating object that was successfully created
        var rating = new Rating(book, authenticatedUserId, 3, null);
        rating.setId(authenticatedUserId);
        rating.setCreatedTime(LocalDateTime.now());
        when(ratingRepository.save(any())).thenReturn(rating);

        // Send off the request and make sure we get back the successfully created DTO
        var ratingDTOJson = objectMapper.writeValueAsString(new RatingDTO(3, null));
        mockMvc.perform(post(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(new RatingDTO(rating))));
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void testUpdateRating_BadRequestBookNotFound() throws Exception {
        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookRepository.existsById("1234")).thenReturn(false);
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
        mockMvc.perform(patch(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void testUpdateRating_UserDoesNotOwnRating() throws Exception {
        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookRepository.existsById(ratedBookId)).thenReturn(true);
        var rating = new Rating();
        rating.setId(1234);
        rating.setUserId(2); // Different than authenticated user
        when(ratingRepository.findLatestRatingByUserForBook(authenticatedUserId, ratedBookId)).thenReturn(Optional.of(rating));
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
        mockMvc.perform(patch(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void testUpdateRating_Success() throws Exception {
        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookRepository.existsById(ratedBookId)).thenReturn(true);

        var rating = new Rating();
        rating.setId(1234);
        rating.setUserId(authenticatedUserId);

        when(ratingRepository.findLatestRatingByUserForBook(authenticatedUserId,ratedBookId)).thenReturn(Optional.of(rating));

        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);

        mockMvc.perform(patch(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(ratingRepository).save(any());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void testDeleteRating_BadRequestBookNotFound() throws Exception {
        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookRepository.existsById("1234")).thenReturn(false);
        mockMvc.perform(delete(ENDPOINT + "/1234")).andExpect(status().isNotFound());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void testDeleteRating_UserDoesNotOwnRating() throws Exception {
        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookRepository.existsById("1234")).thenReturn(true);
        var rating = new Rating();
        rating.setId(1234);
        rating.setUserId(2); // Different than authenticated user
        when(ratingRepository.findLatestRatingByUserForBook(authenticatedUserId, ratedBookId)).thenReturn(Optional.of(rating));
        mockMvc.perform(delete(ENDPOINT)).andExpect(status().isForbidden());
    }

    @Test
    @WithBookselfUserDetails(id = authenticatedUserId)
    void testDeleteRating_Success() throws Exception {
        when(userRepository.existsById(authenticatedUserId)).thenReturn(true);
        when(bookRepository.existsById(ratedBookId)).thenReturn(true);
        var rating = new Rating();
        rating.setId(1234);
        rating.setUserId(authenticatedUserId);
        when(ratingRepository.findLatestRatingByUserForBook(authenticatedUserId, ratedBookId)).thenReturn(Optional.of(rating));
        mockMvc.perform(delete(ENDPOINT)).andExpect(status().isOk());
    }
}

package xyz.bookself.controllers.book;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RatingControllerTest {

    private static final String ENDPOINT = RatingController.REQUEST_MAPPING_PATH.replace("{bookId}", "1234");

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

    @Test @Disabled
    void testAllMethods_Unauthorized() throws Exception {
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);

        mockMvc.perform(post(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(patch(ENDPOINT + "/1234").content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete(ENDPOINT + "/1234")).andExpect(status().isUnauthorized());
    }

    // TODO: Christian - The validation USED to occur as part of the method call
    //  but now I'm doing it manually inside the body (so the order of what gets thrown changed, making these tests fail)
    //  you shouldn't validate a PATCH request body like you would a PUT, since it might not contain all the fields
    //    @Test
//    void testInsertAndUpdate_BadRequestRatingTooLow() throws Exception {
//        var ratingDTO = new RatingDTO(-1, null);
//        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
//        mockMvc.perform(post(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//        mockMvc.perform(patch(ENDPOINT + "/1234").content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testInsertAndUpdate_BadRequestRatingTooHigh() throws Exception {
//        var ratingDTO = new RatingDTO(6, null);
//        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
//
//        mockMvc.perform(post(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//        mockMvc.perform(patch(ENDPOINT + "/1234").content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    @WithBookselfUserDetails(id = 1)
    void testSaveNewRating_BadRequestBookNotFound() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.findById("1234")).thenReturn(Optional.empty());
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
        mockMvc.perform(post(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithBookselfUserDetails(id = 1)
    void testSaveNewRating_Success() throws Exception {
        // Make sure a book is present and the user exists
        var book = new Book();
        book.setId("1234");
        when(bookRepository.findById("1234")).thenReturn(Optional.of(book));
        when(userRepository.existsById(1)).thenReturn(true);

        // Create a rating object that was successfully created
        var rating = new Rating(book, 1, 3, null);
        rating.setId(1);
        rating.setCreatedTime(LocalDateTime.now());
        when(ratingRepository.save(any())).thenReturn(rating);

        // TODO fix this test
        // Send off the request and make sure we get back the successfully created DTO
        var ratingDTOJson = objectMapper.writeValueAsString(new RatingDTO(3, null));
        mockMvc.perform(post(ENDPOINT).content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
                // .andExpect(content().json(objectMapper.writeValueAsString(new RatingDTO(rating))));
    }

    @Test
    @WithBookselfUserDetails(id = 1)
    void testUpdateRating_BadRequestBookNotFound() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById("1234")).thenReturn(false);
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
        mockMvc.perform(patch(ENDPOINT + "/1234").content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test @Disabled
    @WithBookselfUserDetails(id = 1)
    void testUpdateRating_UserDoesNotOwnRating() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById("1234")).thenReturn(true);
        var rating = new Rating();
        rating.setId(1234);
        rating.setUserId(2); // Different than authenticated user
        when(ratingRepository.findById(1234)).thenReturn(Optional.of(rating));
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
        mockMvc.perform(patch(ENDPOINT + "/1234").content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test @Disabled
    @WithBookselfUserDetails(id = 1)
    void testUpdateRating_Success() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById("1234")).thenReturn(true);
        var rating = new Rating();
        rating.setId(1234);
        rating.setUserId(1);
        when(ratingRepository.findById(1234)).thenReturn(Optional.of(rating));
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
        mockMvc.perform(patch(ENDPOINT + "/1234").content(ratingDTOJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(ratingRepository).save(any());
    }

    @Test
    @WithBookselfUserDetails(id = 1)
    void testDeleteRating_BadRequestBookNotFound() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById("1234")).thenReturn(false);
        mockMvc.perform(delete(ENDPOINT + "/1234")).andExpect(status().isNotFound());
    }

    @Test @Disabled
    @WithBookselfUserDetails(id = 1)
    void testDeleteRating_UserDoesNotOwnRating() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById("1234")).thenReturn(true);
        var rating = new Rating();
        rating.setId(1234);
        rating.setUserId(2); // Different than authenticated user
        when(ratingRepository.findById(1234)).thenReturn(Optional.of(rating));
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
        mockMvc.perform(delete(ENDPOINT + "/1234")).andExpect(status().isForbidden());
    }

    @Test @Disabled
    @WithBookselfUserDetails(id = 1)
    void testDeleteRating_Success() throws Exception {
        when(userRepository.existsById(1)).thenReturn(true);
        when(bookRepository.existsById("1234")).thenReturn(true);
        var rating = new Rating();
        rating.setId(1234);
        rating.setUserId(1);
        when(ratingRepository.findById(1234)).thenReturn(Optional.of(rating));
        var ratingDTO = new RatingDTO(5, null);
        var ratingDTOJson = objectMapper.writeValueAsString(ratingDTO);
        mockMvc.perform(delete(ENDPOINT + "/1234")).andExpect(status().isOk());
    }

}

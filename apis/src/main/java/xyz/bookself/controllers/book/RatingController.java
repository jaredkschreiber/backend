package xyz.bookself.controllers.book;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.bookself.books.domain.Rating;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.books.repository.RatingRepository;
import xyz.bookself.exceptions.BadRequestException;
import xyz.bookself.exceptions.ForbiddenException;
import xyz.bookself.exceptions.NotFoundException;
import xyz.bookself.exceptions.UnauthorizedException;
import xyz.bookself.security.BookselfUserDetails;
import xyz.bookself.users.repository.UserRepository;

import javax.validation.Valid;

@RestController
@RequestMapping(RatingController.REQUEST_MAPPING_PATH)
@Slf4j
public class RatingController {
    public static final String REQUEST_MAPPING_PATH = "/v1/books/{bookId}/rating";

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public RatingController(RatingRepository ratingRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @PostMapping
    public ResponseEntity<Void> saveNewRating(@PathVariable("bookId") String bookId,
                                              @AuthenticationPrincipal BookselfUserDetails userDetails,
                                              @RequestBody @Valid RatingDTO ratingDTO) {
        // Make sure the user is authenticated and known
        if (userDetails == null || !userRepository.existsById(userDetails.getId())) {
            throw new UnauthorizedException();
        }
        // Make sure we're adding a rating to a known book
        var book = bookRepository.findById(bookId).orElseThrow(BadRequestException::new);

        var rating = new Rating(book, userDetails.getId(), ratingDTO.getRating(), ratingDTO.getComment());
        ratingRepository.save(rating);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{ratingId}")
    public ResponseEntity<Void> updateRating(@PathVariable("bookId") String bookId,
                                             @AuthenticationPrincipal BookselfUserDetails userDetails,
                                             @PathVariable("ratingId") Integer ratingId,
                                             @RequestBody @Valid RatingDTO ratingDTO) {
        // Make sure the user is authenticated and known
        if (userDetails == null || !userRepository.existsById(userDetails.getId())) {
            throw new UnauthorizedException();
        }
        // Really no need for this since if a book is removed the related ratings are cascade deleted... but doesn't hurt!
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundException();
        }
        // Find the rating and verify the authenticated user owns it
        var rating = ratingRepository.findById(ratingId).orElseThrow(NotFoundException::new);
        if (!userDetails.getId().equals(rating.getUserId())) {
            throw new ForbiddenException();
        }

        rating.setRating(ratingDTO.getRating());
        rating.setComment(ratingDTO.getComment());
        ratingRepository.save(rating);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(@PathVariable("bookId") String bookId,
                                             @AuthenticationPrincipal BookselfUserDetails userDetails,
                                             @PathVariable("ratingId") Integer ratingId) {
        // Make sure the user is authenticated and known
        if (userDetails == null || !userRepository.existsById(userDetails.getId())) {
            throw new UnauthorizedException();
        }
        // Really no need for this since if a book is removed the related ratings are cascade deleted... but doesn't hurt!
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundException();
        }
        // Find the rating and verify the authenticated user owns it
        var rating = ratingRepository.findById(ratingId).orElseThrow(NotFoundException::new);
        if (!userDetails.getId().equals(rating.getUserId())) {
            throw new ForbiddenException();
        }

        ratingRepository.delete(rating);
        return ResponseEntity.ok().build();
    }

}

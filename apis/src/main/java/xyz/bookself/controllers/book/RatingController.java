package xyz.bookself.controllers.book;

import com.fasterxml.jackson.annotation.JsonView;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xyz.bookself.books.domain.Rating;
import xyz.bookself.books.domain.RatingDTOViews;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.books.repository.RatingRepository;
import xyz.bookself.exceptions.BadRequestException;
import xyz.bookself.exceptions.ForbiddenException;
import xyz.bookself.exceptions.NotFoundException;
import xyz.bookself.exceptions.UnauthorizedException;
import xyz.bookself.security.BookselfUserDetails;
import xyz.bookself.users.repository.UserRepository;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping(RatingController.REQUEST_MAPPING_PATH)
@Slf4j
@Timed
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
    
    @JsonView(RatingDTOViews.Rating_Comment_CreatedTimeView.class)
    @GetMapping
    public Rating getRating(@PathVariable("bookId") String bookId,
                            @AuthenticationPrincipal BookselfUserDetails userDetails
    ) {
        // Make sure the user is authenticated and known
        throwIfUserDoesNotExist(userDetails);

        // Make sure it's a known book
        var book = bookRepository.findById(bookId).orElseThrow(BadRequestException::new);

        // have they reviewed this book before?
        return ratingRepository.findLatestRatingByUserForBook(userDetails.getId(), bookId).orElseThrow(NotFoundException::new);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(RatingDTOViews.Rating_Comment_CreatedTimeView.class)
    @PostMapping
    public Rating saveNewRating(@PathVariable("bookId") String bookId,
                                @AuthenticationPrincipal BookselfUserDetails userDetails,
                                @RequestBody @Valid RatingDTO ratingDTO) {
        // Make sure the user is authenticated and known
        throwIfUserDoesNotExist(userDetails);

        // Make sure we're adding a rating to a known book
        var book = bookRepository.findById(bookId).orElseThrow(BadRequestException::new);

        // TODO make sure the user didn't already rate this book

        var rating = new Rating(book, userDetails.getId(), ratingDTO.getRating(), ratingDTO.getComment());
        return ratingRepository.save(rating);
    }


    @PatchMapping(consumes = "application/json") // TODO: think consumes is unnecessary, was added while debugging
    public ResponseEntity<Void> updateRating(@PathVariable("bookId") String bookId,
                                             @AuthenticationPrincipal BookselfUserDetails userDetails,
                                             @RequestBody Map<String, Object> json) {
        // Make sure the user is authenticated and known
        throwIfUserDoesNotExist(userDetails);

        // Really no need for this since if a book is removed the related ratings are cascade deleted... but doesn't hurt!
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundException();
        }

        var rating = ratingRepository.findLatestRatingByUserForBook(userDetails.getId(), bookId).orElseThrow(NotFoundException::new);
        if (!userDetails.getId().equals(rating.getUserId())) {
            throw new ForbiddenException();
        }

        // should only be able to PATCH:
        //  Integer rating
        //  String comment

        // probably a better way to do this...
        if (json.containsKey("rating")) {
            if (!(json.get("rating") instanceof Integer)) {
                throw new BadRequestException();
            }

            Integer ratingFromBody = (Integer) json.get("rating");
            if (ratingFromBody <= 0 || ratingFromBody > 5) {
                throw new BadRequestException();
            }

            rating.setRating(ratingFromBody);
        }

        if (json.containsKey("comment")) { // not else if for a reason
            if (json.get("comment") == null) { // null is actually valid for a comment
                rating.setComment(null);
            }
            else {
                if (!(json.get("comment") instanceof String)) {
                    throw new BadRequestException();
                }

                String commentFromBody = (String) json.get("comment");
                rating.setComment(commentFromBody);
            }
        }

        ratingRepository.save(rating);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRating(@PathVariable("bookId") String bookId,
                                             @AuthenticationPrincipal BookselfUserDetails userDetails) {
        // Make sure the user is authenticated and known
        throwIfUserDoesNotExist(userDetails);
        // Really no need for this since if a book is removed the related ratings are cascade deleted... but doesn't hurt!
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundException();
        }

        // Find the rating
        var rating = ratingRepository.findLatestRatingByUserForBook(userDetails.getId(), bookId).orElseThrow(NotFoundException::new);
        if (!userDetails.getId().equals(rating.getUserId())) {
            throw new ForbiddenException();
        }

        ratingRepository.delete(rating);
        return ResponseEntity.ok().build();
    }

    private void throwIfUserDoesNotExist(@AuthenticationPrincipal BookselfUserDetails userDetails) {
        if (userDetails == null || !userRepository.existsById(userDetails.getId())) {
            throw new UnauthorizedException();
        }
    }

}

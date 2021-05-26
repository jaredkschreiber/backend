package xyz.bookself.controllers.book;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import xyz.bookself.books.domain.AverageRating;
import xyz.bookself.books.domain.Book;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

@Getter
public class BookDTO {

    @JsonProperty("id")
    private final String id;
    @JsonProperty("title")
    private final String title;
    @JsonProperty("genres")
    private final Set<String> genres;
    @JsonProperty("authors")
    private final Set<AuthorDTO> authors;
    @JsonProperty("blurb")
    private final String blurb;
    @JsonProperty("pages")
    private final int pages;
    @JsonProperty("published")
    private final LocalDate datePublished;
    @JsonProperty("ratings")
    private final Set<RatingDTO> ratings;
    @JsonProperty("averageRating")
    private final Double averageRating;

    @JsonCreator
    public BookDTO(@JsonProperty("id") String id,
                   @JsonProperty("title") String title,
                   @JsonProperty("genres") Set<String> genres,
                   @JsonProperty("authors") Set<AuthorDTO> authors,
                   @JsonProperty("blurb") String blurb,
                   @JsonProperty("pages") int pages,
                   @JsonProperty("published") LocalDate datePublished,
                   @JsonProperty("ratings") Set<RatingDTO> ratings,
                   @JsonProperty("averageRating") Double averageRating) {
        this.id = id;
        this.title = title;
        this.genres = genres;
        this.authors = authors;
        this.blurb = blurb;
        this.pages = pages;
        this.datePublished = datePublished;
        this.ratings = ratings;
        this.averageRating = averageRating;
    }

    public BookDTO(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.genres = book.getGenres() == null ? emptySet() : book.getGenres();
        this.authors =  book.getAuthors() == null ? emptySet() : book.getAuthors().stream().map(AuthorDTO::new).collect(Collectors.toSet());
        this.blurb = book.getBlurb();
        this.pages = book.getPages();
        this.datePublished = book.getPublished();
        this.ratings = book.getRatings() == null ? emptySet() : book.getRatings().stream().map(RatingDTO::new).collect(Collectors.toSet());
        this.averageRating = Optional.of(book).map(Book::getAverageRating).map(AverageRating::getAverageRating).orElse(null); // Mapping so we can flatten, still a null if not found
    }

}

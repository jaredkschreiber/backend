package xyz.bookself.controllers.book;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import xyz.bookself.books.domain.GenrePopularity;
import xyz.bookself.books.domain.Popularity;

@Getter
public class PopularityDTO {
    @JsonProperty("book_id")
    private String bookId;
    @JsonProperty("rank")
    private Integer rank;

    @JsonCreator
    public PopularityDTO(@JsonProperty("book_id") String bookId, @JsonProperty("rank") Integer rank) {
        this.bookId = bookId;
        this.rank = rank;
    }

    public PopularityDTO(Popularity popularity) {
        this.bookId = popularity.getBook().getId();
        this.rank = popularity.getRank();
    }

    public PopularityDTO(GenrePopularity popularity) {
        this.bookId = popularity.getBook().getId();
        this.rank = popularity.getRank();
    }
}

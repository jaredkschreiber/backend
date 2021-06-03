package xyz.bookself.controllers.book;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.domain.GenrePopularity;
import xyz.bookself.books.domain.Popularity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class PopularityDTOTest {

    @Test
    void validDTOIsCreatedFromPopularity() {
        final String bookId = "1234";
        final Integer rank = 3;
        final Popularity popularity = new Popularity();
        final Book book = new Book();
        book.setId(bookId);
        popularity.setBook(book);
        popularity.setRank(rank);
        final PopularityDTO dto = new PopularityDTO(popularity);
        assertThat(dto.getBookId()).isEqualTo(bookId);
        assertThat(dto.getRank()).isEqualTo(rank);
    }

    @Test
    void validDTOIsCreatedFromGenrePopularity() {
        final String bookId = "1234";
        final Integer rank = 3;
        final GenrePopularity popularity = new GenrePopularity();
        final Book book = new Book();
        book.setId(bookId);
        popularity.setBook(book);
        popularity.setRank(rank);
        final PopularityDTO dto = new PopularityDTO(popularity);
        assertThat(dto.getBookId()).isEqualTo(bookId);
        assertThat(dto.getRank()).isEqualTo(rank);
    }
}

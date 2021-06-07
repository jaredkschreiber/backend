package xyz.bookself.controllers.book;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookWithRankDTO implements Comparable<BookWithRankDTO> {
    public BookDTO book;
    public Double rank;

    @Override
    public int compareTo(BookWithRankDTO o) {
        return Double.compare(this.rank, o.rank);
    }
}

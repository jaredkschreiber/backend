package xyz.bookself.controllers.book;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BookWithRankDTO {
    public BookDTO book;
    public Double rank;
}

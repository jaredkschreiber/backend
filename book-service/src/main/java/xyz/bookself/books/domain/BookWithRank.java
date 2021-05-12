package xyz.bookself.books.domain;

// a Book with its rank attached
public class BookWithRank {
    public Book book;
    public Double rank;

    public BookWithRank(Book book, Double rank) {
        this.book = book; this.rank = rank;
    }
}

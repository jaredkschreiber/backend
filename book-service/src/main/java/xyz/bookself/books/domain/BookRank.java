package xyz.bookself.books.domain;

// target of interface-based projection: findBooksByQuery()
public interface BookRank {
    Double getRank();
    String getId();
}

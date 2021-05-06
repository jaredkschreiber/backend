package xyz.bookself.books.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

class BookListType {
    public enum ListType {
        READ, READING, DNF, TOREAD;
    }
}

@Data
@NoArgsConstructor
@Entity
@Table(name = "booklists")
public class BookList {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private BookListType.ListType list_type;
    private int num_books;
    @ManyToOne()
    @JoinTable(name = "users_booklists", joinColumns = @JoinColumn(name = "booklists_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private User user;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "books_booklists", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "booklists_id"))
    private List<Book> books = new ArrayList<Book>();

}

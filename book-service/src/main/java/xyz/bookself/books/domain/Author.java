package xyz.bookself.books.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({ "books" })
@Entity
@Table(name = "authors")
public class Author {
    @Id
    private String id;
    private String name;
    @ManyToMany(mappedBy = "authors")
    private Set<Book> books;
}

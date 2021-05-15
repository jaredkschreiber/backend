package xyz.bookself.users.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@TypeDef(
        name = "book_list_type",
        typeClass = BookListEnumConvert.class
)
@Table(name = "book_lists")
public class BookList {
    @Id
    private String id;
    private Integer userId;
    private String bookListName;
    @Enumerated(EnumType.STRING)
    @Type( type = "book_list_type" )
    private BookListEnum listType;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "books_in_list", joinColumns = @JoinColumn(name = "list_id"))
    @Column(name = "book_in_list")
    private Set<String> books= new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookList bookList = (BookList) o;
        return id.equals(bookList.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}




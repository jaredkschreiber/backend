package xyz.bookself.users.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

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
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "book_lists")
public class BookList {
    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    private BookListEnum listType;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "books_in_list", joinColumns = @JoinColumn(name = "list_id"))
    @Column(name = "book_in_list")
    private Set<String> books= new HashSet<>();

}




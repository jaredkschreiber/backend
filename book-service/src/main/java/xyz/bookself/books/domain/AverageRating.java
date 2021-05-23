package xyz.bookself.books.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "book_average_ratings")
public class AverageRating {
    /**
     * This is filled in by JPA via the book reference just below
     */
    @Id
    @Column(name = "book_id")
    @JsonIgnore
    private String bookId;

    @OneToOne
    @JoinColumn(name = "book_id")
    @MapsId
    @JsonIgnore
    private Book book;

    @Column(name = "average_rating", columnDefinition = "numeric(4, 2)", nullable = false)
    private Double averageRating;
}

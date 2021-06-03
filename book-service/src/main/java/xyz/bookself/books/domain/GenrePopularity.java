package xyz.bookself.books.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = GenrePopularity.TABLE_NAME)
public class GenrePopularity {

    public static final String TABLE_NAME = "popular_books_by_genre";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "genre", nullable = false)
    private String genre;
    @OneToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    @Column(name = "rank")
    private Integer rank;
    @Column(name = "ranked_time")
    private LocalDateTime rankedTime;
}

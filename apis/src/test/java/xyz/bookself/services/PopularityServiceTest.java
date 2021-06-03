package xyz.bookself.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.domain.GenrePopularity;
import xyz.bookself.books.domain.Popularity;
import xyz.bookself.books.domain.Rating;
import xyz.bookself.books.repository.GenrePopularityRepository;
import xyz.bookself.books.repository.PopularityRepository;
import xyz.bookself.books.repository.RatingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class PopularityServiceTest {

    private final String cronTaskString = "xyz.bookself.services.PopularityService.computePopularityRanks";

    @Value("${bookself.api.popularity-cron-schedule}")
    private String cronExpression;

    @Autowired
    private ScheduledTaskHolder scheduledTaskHolder;

    @Autowired
    private PopularityService popularityService;

    @MockBean
    private RatingRepository ratingRepository;

    @MockBean
    private PopularityRepository popularityRepository;

    @MockBean
    private GenrePopularityRepository genrePopularityRepository;

    @MockBean
    private TimestampFactory timestampFactory;

    @Test
    void popularityComputationScheduledTaskIsProperlySetup() {
        assertThat(scheduledTaskHolder.getScheduledTasks()
                .stream()
                .filter(task -> task.getTask() instanceof CronTask)
                .map(task -> (CronTask) task.getTask())
                .filter(cronTask -> cronTask.getExpression().equals(cronExpression))
                .filter(cronTask -> cronTask.toString().equals(cronTaskString))
                .count()
        ).isEqualTo(1L);
    }

    @Test
    void givenRatings_whenScheduledTaskRuns_thenRanksAreGenerated() {

        init();

        popularityService.computePopularityRanks();

        verify(ratingRepository, times(1)).findAll();
        verify(popularityRepository, times(2)).save(any());
        verify(genrePopularityRepository, times(4)).save(any());
    }

    /**
     *    ratings:                           genres of the books:
     *    | book_id | user_id | rank |       | book_id | genres        |
     *    |   b1    |   20    |  5   |       |    b1   | War, Peace    |
     *    |   b1    |   30    |  4   |       |    b2   | War, Politics |
     *    |   b1    |   21    |  3   |   ---------------------------------------
     *    |   b2    |   20    |  4   |                 |      War -> b1, b2
     *    |   b2    |   21    |  5   |       inverted: | Politics -> b2
     *                                                 |    Peace -> b1
     */
    private void init() {

        when(timestampFactory.getTimestamp()).thenReturn(LocalDateTime.MIN);

        mockRatingRepository();
        mockPopularityRepository();
        mockGenrePopularityRepository();

        b1.setId("b1");
        b1.setGenres(Set.of("War", "Peace"));

        b2.setId("b2");
        b2.setGenres(Set.of("War", "Politics"));

        p1.setBook(b1);
        p1.setRank(2);
        p1.setRankedTime(timestampFactory.getTimestamp());

        p2.setBook(b2);
        p2.setRank(1);
        p2.setRankedTime(timestampFactory.getTimestamp());

        gp1.setGenre("War");
        gp1.setBook(b2);
        gp1.setRank(1);
        gp1.setRankedTime(timestampFactory.getTimestamp());

        gp2.setGenre("War");
        gp2.setBook(b1);
        gp2.setRank(2);
        gp2.setRankedTime(timestampFactory.getTimestamp());

        gp3.setGenre("Politics");
        gp3.setBook(b2);
        gp3.setRank(1);
        gp3.setRankedTime(timestampFactory.getTimestamp());

        gp4.setGenre("Peace");
        gp4.setBook(b1);
        gp4.setRank(1);
        gp4.setRankedTime(timestampFactory.getTimestamp());
    }

    private void mockRatingRepository() {
        final Rating r1 = new Rating(b1, 20, 5, "comment");
        final Rating r2 = new Rating(b1, 30, 4, "comment");
        final Rating r3 = new Rating(b1, 21, 3, "comment");
        final Rating r4 = new Rating(b2, 20, 4, "comment");
        final Rating r5 = new Rating(b1, 21, 5, "comment");

        when(ratingRepository.findAll()).thenReturn(List.of(r1, r2, r3, r4, r5));
    }

    private void mockPopularityRepository() {
        when(popularityRepository.save(p1)).thenReturn(p1);
        when(popularityRepository.save(p2)).thenReturn(p2);
    }

    private void mockGenrePopularityRepository() {
        when(genrePopularityRepository.save(gp1)).thenReturn(gp1);
        when(genrePopularityRepository.save(gp2)).thenReturn(gp2);
        when(genrePopularityRepository.save(gp3)).thenReturn(gp3);
        when(genrePopularityRepository.save(gp4)).thenReturn(gp4);
    }

    private final Book b1 = new Book();
    private final Book b2 = new Book();
    private final Popularity p1 = new Popularity();
    private final Popularity p2 = new Popularity();
    private final GenrePopularity gp1 = new GenrePopularity();
    private final GenrePopularity gp2 = new GenrePopularity();
    private final GenrePopularity gp3 = new GenrePopularity();
    private final GenrePopularity gp4 = new GenrePopularity();
}

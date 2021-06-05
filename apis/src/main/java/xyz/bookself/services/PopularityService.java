package xyz.bookself.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.domain.GenrePopularity;
import xyz.bookself.books.domain.Popularity;
import xyz.bookself.books.domain.Rating;
import xyz.bookself.books.repository.GenrePopularityRepository;
import xyz.bookself.books.repository.PopularityRepository;
import xyz.bookself.books.repository.RatingRepository;
import xyz.bookself.config.BookselfApiConfiguration;
import xyz.bookself.controllers.book.BookDTO;
import xyz.bookself.controllers.book.PopularityDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
public class PopularityService {

    private final RatingRepository ratingRepository;
    private final BookselfApiConfiguration apiConfiguration;
    private final PopularityRepository popularityRepository;
    private final GenrePopularityRepository genrePopularityRepository;
    private final TimestampFactory timestampFactory;

    public PopularityService(RatingRepository ratingRepository,
                             BookselfApiConfiguration configuration,
                             PopularityRepository popularityRepository,
                             GenrePopularityRepository genrePopularityRepository,
                             TimestampFactory timestampFactory) {
        this.ratingRepository = ratingRepository;
        this.apiConfiguration = configuration;
        this.popularityRepository = popularityRepository;
        this.genrePopularityRepository = genrePopularityRepository;
        this.timestampFactory = timestampFactory;
    }

    @Scheduled(cron = "${bookself.api.popularity-cron-schedule}")
    public void computePopularityRanks() {
        log.info("Begin computing popularity...");
        final Map<Book, Set<Rating>> ratingsGroupedByBooks = getRatingsGroupedByBook();
        final Map<Book, Double> averageRatings = new HashMap<>();

        ratingsGroupedByBooks.keySet().forEach(book -> {
            final Set<Rating> ratings = ratingsGroupedByBooks.get(book);
            final Double averageRating = ratings.stream()
                    .map(Rating::getRating)
                    .reduce(0, Integer::sum)
                    * (1.0 / ratings.size());
            averageRatings.put(book, averageRating);
        });
        log.info("Done computing popularity...");

        final Map<Book, Double> rankedBooks = averageRatings.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(apiConfiguration.getMaxPopularBooksCount())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        log.info("Done ranking books by popularity...");

        // Remove previous rankings - no history :(
        popularityRepository.clean();
        log.info("Done deleting previous popularity ranks...");

        rankedBooks.keySet()
                .forEach(withRankCounter((rank, book) -> {
                    final Popularity popularity = new Popularity();
                    popularity.setBook(book);
                    popularity.setRank(rank);
                    popularity.setRankedTime(timestampFactory.getTimestamp());
                    popularityRepository.save(popularity);
                }));
        log.info("Done persisting popularity ranks...");

        final Map<String, List<String>> inverted = invertBookRankingToGenreRanking(rankedBooks);

        // Remove previous rankings - no history :(
        genrePopularityRepository.clean();
        log.info("Done deleting previous genre popularity ranks...");

        inverted.keySet().forEach(genre -> inverted.get(genre).forEach(withRankCounter((rank, book) -> {
                final GenrePopularity genrePopularity = new GenrePopularity();
                final Book b = new Book();
                b.setId(book);
                genrePopularity.setGenre(genre);
                genrePopularity.setBook(b);
                genrePopularity.setRank(rank);
                genrePopularity.setRankedTime(timestampFactory.getTimestamp());
                genrePopularityRepository.save(genrePopularity);
            }))
        );
        log.info("Done persisting genre popularity ranks...");
    }

    public Collection<BookDTO> findPopularBooks() {
        return popularityRepository
                .getPopularBooks(apiConfiguration.getMaxPopularBooksCount())
                .stream()
                .map(Popularity::getBook)
                .map(BookDTO::new)
                .collect(Collectors.toList());
    }

    public Collection<BookDTO> findPopularBooksByGenre(String genre) {
        return genrePopularityRepository
                .getPopularBooksByGenre(genre, apiConfiguration.getMaxPopularBooksByGenreCount())
                .stream()
                .map(GenrePopularity::getBook)
                .map(BookDTO::new)
                .collect(Collectors.toList());
    }

    private Map<Book, Set<Rating>> getRatingsGroupedByBook() {
        final Collection<Rating> ratings = new HashSet<>();
        ratingRepository.findAll().forEach(ratings::add);
        return ratings.stream().collect(groupingBy(Rating::getBook, toSet()));
    }

    private static <T> Consumer<T> withRankCounter(BiConsumer<Integer, T> consumer) {
        AtomicInteger counter = new AtomicInteger(1);
        return item -> consumer.accept(counter.getAndIncrement(), item);
    }

    private static Map<String, List<String>> invertBookRankingToGenreRanking(Map<Book, Double> rankedBooks) {
        final Map<String, List<String>> invertedIndex = new HashMap<>();
        rankedBooks.keySet().forEach(book -> book.getGenres().forEach(genre -> {
                if(!invertedIndex.containsKey(genre)) {
                    invertedIndex.put(genre, new ArrayList<>(List.of(book.getId())));
                } else {
                    invertedIndex.get(genre).add(book.getId());
                }
            })
        );
        return invertedIndex;
    }
}


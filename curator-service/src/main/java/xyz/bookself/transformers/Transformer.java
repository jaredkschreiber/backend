package xyz.bookself.transformers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.bookself.books.domain.Author;
import xyz.bookself.books.domain.Book;
import xyz.bookself.entities.ScrapedAuthor;
import xyz.bookself.entities.ScrapedBook;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class Transformer {

    public Book transformToBook(ScrapedBook scrapedBook) {
        final Book book = new Book();
        book.setId(scrapedBook.getId());
        book.setTitle(scrapedBook.getTitle());
        book.setPages(scrapedBook.getPages());
        book.setGenres(scrapedBook.getGenres());
        book.setBlurb(scrapedBook.getBlurb());
        book.setAuthors(transformToAuthor(scrapedBook.getAuthors()));
        book.setPublished(transformToLocalDate(scrapedBook.getPublished()));
        return book;
    }

    public Set<Author> transformToAuthor(final Set<ScrapedAuthor> scrapedAuthors) {
        final Set<Author> authors = new HashSet<>();
        scrapedAuthors.stream().map(s -> {
            final Author a = new Author();
            a.setName(s.getName());
            a.setId(s.getId());
            a.setBooks(new HashSet<>());
            return a;
        }).forEach(authors::add);
        return authors;
    }

    public LocalDate transformToLocalDate(String dateString) {

        final String datePublished = formatDate(dateString);

        return (datePublished.matches("\\d{4}-\\d{2}-\\d{1,2}"))
                ? LocalDate.parse(datePublished, DateTimeFormatter.ofPattern("yyyy-MM-d"))
                : null;
    }

    /**
     * Reformats the date in YYYY-MM-DD format
     *
     * @param dateString date expected to be formatted like October 15th 1985
     * @return date in YYYY-MM-DD format or empty string
     */
    private String formatDate(String dateString) {

        /*
         * All known published date patterns in the collection:
         * 1. "2021"
         * 2. "May 5th 2021"
         * 3. "May 2021"
         * 4. "May 5th 2021 (first published February 16th 2015)"
         * 5. "May 5th 2021 (first published 2009)"
         * 6. ""
         */
        final Map<Integer, Pattern> datePatterns = new HashMap<>();
        datePatterns.put(1, Pattern.compile("(\\d{4})"));
        datePatterns.put(2, Pattern.compile("(([A-Z][a-z]+)(\\s)(\\d{1,2})([a-z]{2})(\\s)(\\d{4}))"));
        datePatterns.put(3, Pattern.compile("(([A-Z][a-z]+)(\\s)(\\d{4}))"));

        // RegEx group replacements
        final Map<Integer, String> replacements = new HashMap<>();
        replacements.put(1, "$1-12-31");
        replacements.put(2, "$7-$2-$4");
        replacements.put(3, "$4-$2-01");

        final int[] patternPriority = { 2, 3, 1 };

        final StringBuilder sb = new StringBuilder();

        for(int i = 0; i < patternPriority.length && sb.length() == 0; i++) {
            final int index = patternPriority[i];
            final Pattern p = datePatterns.get(index);
            final Matcher m = p.matcher(dateString);
            if (m.find()) {
                sb.append(m.replaceFirst(replacements.get(index)));
            }
        }
        final String formattedDate = replaceMonthNameByNumber(sb.toString());

        final Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{1,2}");
        final Matcher matcher = pattern.matcher(formattedDate);
        return (matcher.find()) ? matcher.group() : "";
    }

    /**
     * Replaces month name by month number.
     * Example:
     * - January is replaced by 01
     * - November is replaced by 11
     * @param dateString
     * @return a date string where month name is replaced by the month number
     */
    private String replaceMonthNameByNumber(String dateString) {

        final Map<String, String> months = Stream.of(new String[][] {
                { "01", "January" }, { "02", "February" }, { "03", "March" }, { "04", "April" },
                { "05", "May" }, { "06", "June" }, { "07", "July" }, { "08", "August" },
                { "09", "September" }, { "10", "October" }, { "11", "November" }, { "12", "December" }
        }).collect(Collectors.toMap(m -> m[1], m -> m[0]));

        for(String n : months.keySet()) {
            if(dateString.contains(n)) {
                return dateString.replaceAll(n, months.get(n));
            }
        }

        return dateString;
    }
}

package xyz.bookself;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xyz.bookself.books.domain.Book;
import xyz.bookself.entities.ScrapedBook;
import xyz.bookself.preprocessors.DateFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@Slf4j
public class CuratorApplication implements CommandLineRunner {

    private static String booksDir = "./";

    private final DateFormatter dateFormatter;

    @Autowired
    public CuratorApplication(DateFormatter formatter) {
        this.dateFormatter = formatter;
    }

    public static void main(String[] args) {
        if(args.length == 0) {
            log.error("Books directory has to be specified.");
            System.exit(1);
        }
        booksDir = args[0];
        SpringApplication.run(CuratorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        final Set<Book> books = new HashSet<>();
        if(Files.isDirectory(Path.of(booksDir))) {
           Files.walk(Path.of(booksDir))
                   .filter(path -> !Files.isDirectory(path))
                   .filter(path -> path.getFileName().toString().endsWith(".json"))
                   .map(path -> {
                       Set<ScrapedBook> scrapedBooks = new HashSet<>();
                       try {
                           final BufferedReader bufferedReader = Files.newBufferedReader(path);
                           final Gson gson = new Gson();
                           scrapedBooks = gson.fromJson(bufferedReader, new TypeToken<Set<ScrapedBook>>() {}.getType());
                       } catch (IOException ignored) {
                       }
                       return scrapedBooks;
                   })
                   .flatMap(Collection::stream)
                   .map(dateFormatter::transformToBook)
                   .forEach(books::add);
        }
        //books.forEach(book -> log.info("Book " + book.getId() + " is published on " + book.getPublished().toString()));
    }
}

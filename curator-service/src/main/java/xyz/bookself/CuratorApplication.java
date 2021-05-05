package xyz.bookself;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.entities.ScrapedBook;
import xyz.bookself.transformers.Transformer;

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

    private final Transformer transformer;
    private final BookRepository bookRepository;

    @Autowired
    public CuratorApplication(Transformer formatter, BookRepository repository) {
        this.transformer = formatter;
        this.bookRepository = repository;
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
        if(Files.isDirectory(Path.of(booksDir))) {
           Files.walk(Path.of(booksDir))
                   .filter(path -> !Files.isDirectory(path))
                   .filter(path -> path.getFileName().toString().startsWith("_"))
                   .filter(path -> path.getFileName().toString().endsWith(".json"))
                   .map(path -> {
                       Set<ScrapedBook> scrapedBooks = new HashSet<>();
                       try {
                           final BufferedReader bufferedReader = Files.newBufferedReader(path);
                           final Gson gson = new Gson();
                           scrapedBooks = gson.fromJson(bufferedReader, new TypeToken<Set<ScrapedBook>>() {}.getType());
                       } catch (IOException ioe) {
                           log.error("File read error: " + path.getFileName().toString());
                       }
                       return scrapedBooks;
                   })
                   .flatMap(Collection::stream)
                   .map(transformer::transformToBook)
                   .forEach(book -> {
                       try {
                           bookRepository.save(book);
                       } catch (Exception e) {
                           log.error("Failed to persist " + book.getId());
                       }
                   });
        }
    }
}

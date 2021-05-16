package xyz.bookself;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xyz.bookself.books.domain.Book;
import xyz.bookself.books.repository.BookRepository;
import xyz.bookself.dto.BookDto;
import xyz.bookself.transformers.Transformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

        final Collection<String> booksInDatabase = bookRepository.findAll()
                .stream()
                .map(Book::getId)
                .collect(Collectors.toSet());

        if(Files.isDirectory(Path.of(booksDir))) {
           Files.walk(Path.of(booksDir))
                   .filter(path -> !Files.isDirectory(path))
                   .filter(path -> path.getFileName().toString().startsWith("_"))
                   .filter(path -> path.getFileName().toString().endsWith(".json"))
                   .map(path -> {
                       Set<BookDto> bookDtos = new HashSet<>();
                       try {
                           final BufferedReader bufferedReader = Files.newBufferedReader(path);
                           final Gson gson = new Gson();
                           bookDtos = gson.fromJson(bufferedReader, new TypeToken<Set<BookDto>>() {}.getType());
                       } catch (IOException ioe) {
                           log.error("File read error: " + path.getFileName().toString());
                       }
                       return bookDtos;
                   })
                   .flatMap(Collection::stream)
                   .map(transformer::transformToBook)
                   .forEach(book -> {
                       try {
                           if(!booksInDatabase.contains(book.getId())) {
                               bookRepository.save(book);
                           } else {
                               log.info(String.format("Book %s is already in database.", book.getId()));
                           }
                       } catch (Exception e) {
                           log.error("Failed to persist " + book.getId());
                       }
                   });
        }
    }
}

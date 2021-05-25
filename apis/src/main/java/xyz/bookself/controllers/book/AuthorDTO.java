package xyz.bookself.controllers.book;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import xyz.bookself.books.domain.Author;

@Getter
public class AuthorDTO {

    @JsonProperty("id")
    private final String id;
    @JsonProperty("name")
    private final String name;

    @JsonCreator
    public AuthorDTO(@JsonProperty("id") String id,
                     @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public AuthorDTO(Author author) {
        this.id = author.getId();
        this.name = author.getName();
    }

}

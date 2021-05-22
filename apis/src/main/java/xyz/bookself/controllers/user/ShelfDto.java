package xyz.bookself.controllers.user;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ShelfDto {
    private String newListName;
    private String newBookListId;
    private Set<String> booksToBeAdded;
    private Set<String> booksToBeRemoved;
}

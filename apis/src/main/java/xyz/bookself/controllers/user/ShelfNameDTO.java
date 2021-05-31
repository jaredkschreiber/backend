package xyz.bookself.controllers.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter

public class ShelfNameDTO {
    @NotBlank
    private String newListName;
}

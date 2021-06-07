package xyz.bookself.controllers.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class ResetPasswordDto {
    private String token;
    private String password;
}

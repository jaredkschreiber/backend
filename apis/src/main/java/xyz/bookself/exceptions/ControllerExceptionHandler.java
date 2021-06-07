package xyz.bookself.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    public Error handleNotFound(HttpServletRequest request, NoSuchElementException exception) {
        log.error(request.getRequestURL().toString(), exception.getLocalizedMessage());
        return new Error(request.getRequestURL().toString(), exception.getLocalizedMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Error handleBadRequest(HttpServletRequest request, NoSuchElementException exception) {
        log.error(request.getRequestURL().toString(), exception.getLocalizedMessage());
        return new Error(request.getRequestURL().toString(), exception.getLocalizedMessage());
    }
}

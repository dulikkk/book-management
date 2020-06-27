package kub4k1.bookmanagement.adapter.incoming.api;

import kub4k1.bookmanagement.domain.book.dto.exception.BookException;
import kub4k1.bookmanagement.domain.book.dto.exception.CannotFindBookException;
import kub4k1.bookmanagement.domain.user.dto.exception.CannotFindUserException;
import kub4k1.bookmanagement.domain.user.dto.exception.CannotSendTokenException;
import kub4k1.bookmanagement.domain.user.dto.exception.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.time.LocalDateTime.now;

@RestControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BookException.class)
    public ResponseEntity<ApiResponse> BookDomainExceptionHandler(BookException e) {
        ApiResponse apiResponse = ApiResponse.builder()
                .content(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse> UserDomainExceptionHandler(UserException e) {
        ApiResponse apiResponse = ApiResponse.builder()
                .content(e.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(CannotFindBookException.class)
    public ResponseEntity<ApiResponse> cannotFindBookExceptionHandler(CannotFindBookException e) {
        ApiResponse apiResponse = ApiResponse.builder()
                .content(e.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(CannotFindUserException.class)
    public ResponseEntity<ApiResponse> cannotFindUserExceptionHandler(CannotFindUserException e) {
        ApiResponse apiResponse = ApiResponse.builder()
                .content(e.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }


    @ExceptionHandler(CannotSendTokenException.class)
    public ResponseEntity<ApiResponse> cannotSendTokenHandler(CannotSendTokenException e) {
        ApiResponse apiResponse = ApiResponse.builder()
                .content(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

}

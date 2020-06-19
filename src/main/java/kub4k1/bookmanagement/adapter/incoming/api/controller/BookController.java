package kub4k1.bookmanagement.adapter.incoming.api.controller;

import kub4k1.bookmanagement.adapter.incoming.api.ApiEndpoints;
import kub4k1.bookmanagement.adapter.incoming.api.ApiResponse;
import kub4k1.bookmanagement.domain.book.BookDomainFacade;
import kub4k1.bookmanagement.domain.book.dto.BookDto;
import kub4k1.bookmanagement.domain.book.dto.ExtendBookCommand;
import kub4k1.bookmanagement.domain.book.dto.NewBookCommand;
import kub4k1.bookmanagement.domain.book.query.BookQueryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.time.LocalDateTime.now;

@RequiredArgsConstructor
@RestController
class BookController {

    private final BookDomainFacade bookDomainFacade;
    private final BookQueryRepository bookQueryRepository;

    @PostMapping(ApiEndpoints.CREATE_BOOK)
    public ResponseEntity<ApiResponse> saveBook(@RequestBody NewBookCommand newBookCommand) {
        String bookId = bookDomainFacade.addNewBook(newBookCommand);

        ApiResponse apiResponse = ApiResponse.builder()
                .content(bookId)
                .timestamp(now())
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(ApiEndpoints.GET_ACTIVE_BOOKS)
    List<BookDto> findAllActiveBooks(@RequestParam(defaultValue = "0") int page, @RequestParam String userId) {
        return bookQueryRepository.findAllActiveBooksByUserId(page, 2, userId);
    }

    @PatchMapping(ApiEndpoints.EXTEND_BOOK)
    ResponseEntity<ApiResponse> extendBook(@RequestBody ExtendBookCommand extendBookCommand) {
        bookDomainFacade.extendDateOfExpiration(extendBookCommand);

        ApiResponse apiResponse = ApiResponse.builder()
                .content("Book has extended successfully")
                .status(HttpStatus.OK.value())
                .timestamp(now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping(ApiEndpoints.RETURN_BOOK)
    ResponseEntity<ApiResponse> returnBook(@RequestParam String id) {
        if (StringUtils.isNotEmpty(id)) {
            bookDomainFacade.returnBook(id);

            ApiResponse apiResponse = ApiResponse.builder()
                    .content("Book has returned successfully")
                    .status(HttpStatus.OK.value())
                    .timestamp(now())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } else {
            ApiResponse errorResponse = ApiResponse.builder()
                    .content("ID cannot be null")
                    .status(HttpStatus.BAD_REQUEST.value())
                    .timestamp(now())
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}

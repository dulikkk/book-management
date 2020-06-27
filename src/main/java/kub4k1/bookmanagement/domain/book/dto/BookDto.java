package kub4k1.bookmanagement.domain.book.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Builder
@Value
public class BookDto {

    String id;

    String userId;

    String title;

    String author;

    LocalDateTime dateOfExpiration;

    BookStatusDto bookStatusDto;
}

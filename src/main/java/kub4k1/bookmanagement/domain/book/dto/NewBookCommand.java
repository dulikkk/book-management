package kub4k1.bookmanagement.domain.book.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Builder
@Value
public class NewBookCommand {

    String userId;

    String title;

    String author;

    LocalDateTime dateOfExpiration;

    public BookDto toDto() {
        return BookDto.builder()
                .userId(userId)
                .author(author)
                .title(title)
                .author(author)
                .dateOfExpiration(dateOfExpiration)
                .bookStatusDto(BookStatusDto.ACTIVE)
                .build();
    }
}

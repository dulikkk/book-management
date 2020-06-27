package kub4k1.bookmanagement.domain.book.dto;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Value
public class ExtendBookCommand {

    String id;

    LocalDateTime newDate;
}

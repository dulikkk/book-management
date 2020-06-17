package kub4k1.bookmanagement.domain.book.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Value
public class ExtendBookCommand {

    String id;

    LocalDateTime newDate;
}

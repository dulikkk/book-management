package kub4k1.bookmanagement.infrastructure.book.mongoDb;

import kub4k1.bookmanagement.domain.book.dto.BookStatusDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Builder
@Getter
@Document("book")
public class BookDocument {

    @Id
    private String id;

    private String userId;

    private final String title;

    private final String author;

    private final LocalDateTime dateOfExpiration;

    @Field("status")
    private final BookStatusDto bookStatusDto;
}

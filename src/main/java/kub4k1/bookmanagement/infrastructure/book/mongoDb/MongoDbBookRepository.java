package kub4k1.bookmanagement.infrastructure.book.mongoDb;

import kub4k1.bookmanagement.domain.book.dto.BookDto;
import kub4k1.bookmanagement.domain.book.port.outgoing.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MongoDbBookRepository implements BookRepository {

    private final MongoTemplate mongoTemplate;
    private final BookConverter bookConverter = new BookConverter();

    @Override
    public String createBook(BookDto bookDto) {
        BookDocument savedBookDocument = mongoTemplate.insert(bookConverter.toDocument(bookDto));

        return savedBookDocument.getId();
    }

    @Override
    public void updateBook(BookDto bookDto) {
        mongoTemplate.save(bookConverter.toDocument(bookDto));
    }
}

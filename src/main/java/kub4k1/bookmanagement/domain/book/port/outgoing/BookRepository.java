package kub4k1.bookmanagement.domain.book.port.outgoing;

import kub4k1.bookmanagement.domain.book.dto.BookDto;

public interface BookRepository {

    String createBook(BookDto bookDto);

    void updateBook(BookDto bookDto);
}

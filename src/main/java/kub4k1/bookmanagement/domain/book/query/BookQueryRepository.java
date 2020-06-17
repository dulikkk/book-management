package kub4k1.bookmanagement.domain.book.query;

import kub4k1.bookmanagement.domain.book.dto.BookDto;

import java.util.List;
import java.util.Optional;

public interface BookQueryRepository {

    List<BookDto> findAllActiveBooksByUserId(int page, int elementPerPage, String userId);

    Optional<BookDto> findById(String id);
}

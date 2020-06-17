package kub4k1.bookmanagement.domain.book;

import kub4k1.bookmanagement.domain.book.dto.BookDto;
import kub4k1.bookmanagement.domain.book.dto.BookStatusDto;
import kub4k1.bookmanagement.domain.book.dto.exception.BookException;
import kub4k1.bookmanagement.domain.book.dto.exception.CannotFindBookException;
import kub4k1.bookmanagement.domain.book.port.outgoing.BookRepository;
import kub4k1.bookmanagement.domain.book.query.BookQueryRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class BookReturner {

    private final BookRepository bookRepository;
    private final BookQueryRepository bookQueryRepository;

    public void returnBook(String id) {
        BookDto returnedBookToUpdate = bookQueryRepository.findById(id)
                .map(this::archiveBook)
                .orElseThrow(() -> new CannotFindBookException(id));

        bookRepository.updateBook(returnedBookToUpdate);
    }

    private BookDto archiveBook(BookDto bookDto) {
        if (bookDto.getBookStatusDto() == BookStatusDto.ARCHIVE) {
            throw new BookException("The book has already been archived");
        } else {
            return BookDto.builder()
                    .id(bookDto.getId())
                    .userId(bookDto.getUserId())
                    .title(bookDto.getTitle())
                    .author(bookDto.getAuthor())
                    .bookStatusDto(BookStatusDto.ARCHIVE)
                    .dateOfExpiration(bookDto.getDateOfExpiration())
                    .build();
        }
    }
}

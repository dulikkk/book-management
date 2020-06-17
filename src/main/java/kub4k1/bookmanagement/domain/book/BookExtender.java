package kub4k1.bookmanagement.domain.book;

import kub4k1.bookmanagement.domain.book.dto.BookDto;
import kub4k1.bookmanagement.domain.book.dto.ExtendBookCommand;
import kub4k1.bookmanagement.domain.book.port.outgoing.BookRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
class BookExtender {

    private final BookRepository bookRepository;
    private final BookValidator bookValidator;

    public void extendDateOfExpiration(ExtendBookCommand extendBookCommand) {
        BookDto extendedBookToUpdate = bookValidator.validateExtendBookCommand(extendBookCommand);
        BookDto extendedBook = extendDateOfExpiration(extendedBookToUpdate, extendBookCommand.getNewDate());
        bookRepository.updateBook(extendedBook);
    }

    private BookDto extendDateOfExpiration(BookDto bookDto, LocalDateTime newDate) {
        return BookDto.builder()
                .id(bookDto.getId())
                .userId(bookDto.getUserId())
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .bookStatusDto(bookDto.getBookStatusDto())
                .dateOfExpiration(newDate)
                .build();
    }

}

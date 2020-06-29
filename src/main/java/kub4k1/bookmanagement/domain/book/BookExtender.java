package kub4k1.bookmanagement.domain.book;

import kub4k1.bookmanagement.domain.book.dto.BookDto;
import kub4k1.bookmanagement.domain.book.dto.ExtendBookCommand;
import kub4k1.bookmanagement.domain.book.dto.exception.CannotFindBookException;
import kub4k1.bookmanagement.domain.book.port.outgoing.BookRepository;
import kub4k1.bookmanagement.domain.book.query.BookQueryRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
class BookExtender {

    private final BookRepository bookRepository;
    private final BookQueryRepository bookQueryRepository;
    private final BookValidator bookValidator;

    public void extendDateOfExpiration(ExtendBookCommand extendBookCommand) {
        bookValidator.validateExtendBookCommand(extendBookCommand);
        BookDto BookByExtendBookCommand = getBookByExtendBookCommand(extendBookCommand);
        BookDto extendedBook = extendDateOfExpiration(BookByExtendBookCommand, extendBookCommand.getNewDate());
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

    private BookDto getBookByExtendBookCommand(ExtendBookCommand extendBookCommand) {
        return bookQueryRepository.findById(extendBookCommand.getId())
                .orElseThrow(() -> new CannotFindBookException(extendBookCommand.getId()));
    }

}

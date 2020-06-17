package kub4k1.bookmanagement.domain.book;

import kub4k1.bookmanagement.domain.book.dto.NewBookCommand;
import kub4k1.bookmanagement.domain.book.port.outgoing.BookRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class BookCreator {

    private final BookRepository bookRepository;
    private final BookValidator bookValidator;

    public String createBook(NewBookCommand newBookCommand) {
        bookValidator.validateNewBook(newBookCommand);
        return bookRepository.createBook(newBookCommand.toDto());
    }
}

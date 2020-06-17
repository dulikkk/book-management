package kub4k1.bookmanagement.domain.book;

import kub4k1.bookmanagement.domain.book.dto.ExtendBookCommand;
import kub4k1.bookmanagement.domain.book.dto.NewBookCommand;
import kub4k1.bookmanagement.domain.book.port.outgoing.BookRepository;
import kub4k1.bookmanagement.domain.book.query.BookQueryRepository;
import kub4k1.bookmanagement.domain.user.query.UserQueryRepository;

public class BookDomainFacade {

    private BookCreator bookCreator;
    private BookExtender bookExtender;
    private BookReturner bookReturner;

    public BookDomainFacade(BookRepository bookRepository, BookQueryRepository bookQueryRepository, UserQueryRepository userQueryRepository) {
        BookValidator bookValidator = new BookValidator(userQueryRepository, bookQueryRepository);
        this.bookCreator = new BookCreator(bookRepository, bookValidator);
        this.bookExtender = new BookExtender(bookRepository, bookValidator);
        this.bookReturner = new BookReturner(bookRepository, bookQueryRepository);
    }

    public String addNewBook(NewBookCommand newBookCommand) {
        return bookCreator.createBook(newBookCommand);
    }

    public void extendDateOfExpiration(ExtendBookCommand extendBookCommand) {
        bookExtender.extendDateOfExpiration(extendBookCommand);
    }

    public void returnBook(String id) {
        bookReturner.returnBook(id);
    }
}

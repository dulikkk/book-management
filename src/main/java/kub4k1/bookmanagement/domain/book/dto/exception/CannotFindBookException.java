package kub4k1.bookmanagement.domain.book.dto.exception;

public class CannotFindBookException extends RuntimeException {

    public CannotFindBookException(String bookId) {
        super("Cannot find book with id: " + bookId);
    }
}

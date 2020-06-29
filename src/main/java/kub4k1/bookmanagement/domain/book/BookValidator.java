package kub4k1.bookmanagement.domain.book;

import kub4k1.bookmanagement.domain.book.dto.BookDto;
import kub4k1.bookmanagement.domain.book.dto.BookStatusDto;
import kub4k1.bookmanagement.domain.book.dto.ExtendBookCommand;
import kub4k1.bookmanagement.domain.book.dto.NewBookCommand;
import kub4k1.bookmanagement.domain.book.dto.exception.BookException;
import kub4k1.bookmanagement.domain.book.dto.exception.CannotFindBookException;
import kub4k1.bookmanagement.domain.book.query.BookQueryRepository;
import kub4k1.bookmanagement.domain.user.dto.exception.CannotFindUserException;
import kub4k1.bookmanagement.domain.user.query.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@RequiredArgsConstructor
class BookValidator {

    private final UserQueryRepository userQueryRepository;
    private final BookQueryRepository bookQueryRepository;

    public void validateNewBook(NewBookCommand newBookCommand) {
        checkIsEmptyOrNull(newBookCommand);

        // check user id
        userQueryRepository.findById(newBookCommand.getUserId())
                .orElseThrow(() -> new CannotFindUserException("This user does not exist"));

        // check date
        checkDateOfExpiration(newBookCommand);
    }

    public void validateExtendBookCommand(ExtendBookCommand extendBookCommand) {
        checkIsEmptyOrNull(extendBookCommand);

        bookQueryRepository.findById(extendBookCommand.getId())
                .map(bookDto -> {
                    if (bookDto.getBookStatusDto().equals(BookStatusDto.ARCHIVE)) {
                        throw new BookException("This book has returned yet");
                    }
                    return bookDto;
                })
                .map(book -> checkNewDateOfExpiration(book, extendBookCommand.getNewDate()));
    }

    private void checkIsEmptyOrNull(NewBookCommand newBookCommand) {
        if (StringUtils.isEmpty(newBookCommand.getUserId())) {
            throw new BookException("User id cannot be null or empty");
        }

        if (StringUtils.isEmpty(newBookCommand.getTitle())) {
            throw new BookException("Title cannot be null or empty");
        }

        if (StringUtils.isEmpty(newBookCommand.getAuthor())) {
            throw new BookException("Author cannot be null or empty");
        }

        if (newBookCommand.getDateOfExpiration() == null) {
            throw new BookException("Date of expiration cannot be null");
        }
    }

    private void checkIsEmptyOrNull(ExtendBookCommand extendBookCommand) {
        if (StringUtils.isEmpty(extendBookCommand.getId())) {
            throw new BookException("Books id cannot be null or empty");
        }

        if (extendBookCommand.getNewDate() == null) {
            throw new BookException("New date cannot be null or empty");
        }
    }

    private void checkDateOfExpiration(NewBookCommand newBookCommand) {
        if (newBookCommand.getDateOfExpiration().isBefore(now())
                || newBookCommand.getDateOfExpiration().isEqual(now())) {
            throw new BookException("Date of expiration cannot be earlier or present");
        }
    }

    private BookDto checkNewDateOfExpiration(BookDto bookDto, LocalDateTime newDate) {
        if (newDate.isBefore(bookDto.getDateOfExpiration()) || newDate.isEqual(bookDto.getDateOfExpiration())) {
            throw new BookException("New date of expiration cannot be earlier or the same");
        }
        return bookDto;
    }
}

package kub4k1.bookmanagement.domain.book

import kub4k1.bookmanagement.domain.book.dto.BookStatusDto
import kub4k1.bookmanagement.domain.book.dto.ExtendBookCommand
import kub4k1.bookmanagement.domain.book.dto.NewBookCommand
import kub4k1.bookmanagement.domain.book.dto.exception.BookException
import kub4k1.bookmanagement.domain.user.dto.RoleDto
import kub4k1.bookmanagement.domain.user.dto.UserDto
import kub4k1.bookmanagement.infrastructure.book.memory.InMemoryBookRepository
import kub4k1.bookmanagement.infrastructure.user.memory.InMemoryUserRepository
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.Month

class BookDomainTest extends Specification {
    InMemoryBookRepository inMemoryBookRepository = new InMemoryBookRepository()

    InMemoryUserRepository inMemoryUserRepository = Mock(InMemoryUserRepository)

    BookDomainFacade bookFacade = new BookDomainConfigurator().bookDomainFacade(inMemoryBookRepository, inMemoryUserRepository)

    @Shared
    String fakeUserId = "abc123"

    UserDto fakeUser = UserDto.builder()
            .username("username")
            .email("email")
            .password("password")
            .roles(Set.of(RoleDto.USER))
            .active(true)
            .id(fakeUserId)
            .build()

    @Shared
    NewBookCommand newBookCommand = NewBookCommand.builder()
            .title("The Old Man and the Sea")
            .author("Ernest Hemingway")
            .dateOfExpiration(LocalDateTime.of(2020, Month.DECEMBER, 20, 23, 59))
            .userId(fakeUserId)
            .build()

    def setup() {
        inMemoryUserRepository.findById(fakeUserId) >> Optional.of(fakeUser)
    }

    def "add new book to system"() {
        when: "trying to add a new book to the system"
        String bookId = bookFacade.addNewBook(newBookCommand)

        then: "the system should has this book"
        inMemoryBookRepository.findById(bookId).isPresent()
    }

    def "add new empty book to the system"() {
        given: "empty newBookCommand"
        NewBookCommand newEmptyBook = NewBookCommand.builder().build()

        when: "trying to add a new empty book to the system"
        bookFacade.addNewBook(newEmptyBook)

        then: "the system should throw an exception"
        thrown(BookException)
    }

    def "extend date of expiration"() {
        given: "added book and new date"
        String bookId = bookFacade.addNewBook(newBookCommand)
        ExtendBookCommand extendBookCommand = new ExtendBookCommand(bookId,
                LocalDateTime.of(2021, Month.DECEMBER, 20, 23, 59))

        when: "trying to extend date of expiration"
        bookFacade.extendDateOfExpiration(extendBookCommand)

        then: "the system should extend date of expiration"
        inMemoryBookRepository.findById(bookId).get().getDateOfExpiration() == extendBookCommand.getNewDate()
    }

    def "extend date of expiration by earlier or same date"() {
        given: "added book and new bad date"
        String bookId = bookFacade.addNewBook(newBookCommand)
        ExtendBookCommand extendBookCommand = new ExtendBookCommand(bookId, date)

        when: "trying to extend date of expiration by earlier date"
        bookFacade.extendDateOfExpiration(extendBookCommand)

        then: "the system should throw an exception with message"
        def exception = thrown(expectedException)
        exception.getMessage() == expectedMessage

        where:
        date                                                || expectedException || expectedMessage
        newBookCommand.getDateOfExpiration().minusMonths(1) || BookException     || "New date of expiration cannot be earlier or the same"
        newBookCommand.getDateOfExpiration()                || BookException     || "New date of expiration cannot be earlier or the same"
    }

    def "return a book"() {
        given: "added book"
        String bookId = bookFacade.addNewBook(newBookCommand)

        when: "trying to return a book"
        bookFacade.returnBook(bookId)

        then: "the book should be an archive"
        inMemoryBookRepository.findById(bookId).get().getBookStatusDto() == BookStatusDto.ARCHIVE
    }

    def "return returned book"() {
        given: "returned book"
        String bookId = bookFacade.addNewBook(newBookCommand)
        bookFacade.returnBook(bookId)

        when: "trying to return this book again"
        bookFacade.returnBook(bookId)

        then: "the system should throw exception with message"
        def exception = thrown(BookException)
        exception.getMessage() == "The book has already been archived"
    }
}

package kub4k1.bookmanagement.domain.book;

import kub4k1.bookmanagement.infrastructure.book.memory.InMemoryBookRepository;
import kub4k1.bookmanagement.infrastructure.user.memory.InMemoryUserRepository;

public class BookDomainConfigurator {

    public static BookDomainFacade bookFacade(InMemoryBookRepository inMemoryBookRepository, InMemoryUserRepository inMemoryUserRepository) {
        return new BookDomainFacade(inMemoryBookRepository, inMemoryBookRepository, inMemoryUserRepository);
    }
}

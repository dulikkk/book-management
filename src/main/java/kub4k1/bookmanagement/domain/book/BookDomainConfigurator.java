package kub4k1.bookmanagement.domain.book;

import kub4k1.bookmanagement.domain.book.port.outgoing.BookRepository;
import kub4k1.bookmanagement.domain.book.query.BookQueryRepository;
import kub4k1.bookmanagement.domain.user.query.UserQueryRepository;
import kub4k1.bookmanagement.infrastructure.book.memory.InMemoryBookRepository;
import kub4k1.bookmanagement.infrastructure.user.memory.InMemoryUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookDomainConfigurator {

    public BookDomainFacade bookDomainFacade(InMemoryBookRepository inMemoryBookRepository, InMemoryUserRepository inMemoryUserRepository) {
        return new BookDomainFacade(inMemoryBookRepository, inMemoryBookRepository, inMemoryUserRepository);
    }

    @Bean
    public BookDomainFacade bookDomainFacade(BookRepository bookRepository, BookQueryRepository bookQueryRepository,
                                       UserQueryRepository userQueryRepository) {
        return new BookDomainFacade(bookRepository, bookQueryRepository, userQueryRepository);
    }
}

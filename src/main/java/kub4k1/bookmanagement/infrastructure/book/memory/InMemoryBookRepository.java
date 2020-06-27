package kub4k1.bookmanagement.infrastructure.book.memory;

import kub4k1.bookmanagement.domain.book.dto.BookDto;
import kub4k1.bookmanagement.domain.book.dto.BookStatusDto;
import kub4k1.bookmanagement.domain.book.port.outgoing.BookRepository;
import kub4k1.bookmanagement.domain.book.query.BookQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;

public class InMemoryBookRepository implements BookRepository, BookQueryRepository {

    private Map<String, BookDto> booksRepo = new ConcurrentHashMap<>();

    @Override
    public String createBook(BookDto bookDto) {
        BookDto bookToSave = BookDto.builder()
                .userId(bookDto.getUserId())
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .bookStatusDto(bookDto.getBookStatusDto())
                .dateOfExpiration(bookDto.getDateOfExpiration())
                .id(randomUUID().toString())
                .build();
        booksRepo.put(bookToSave.getId(), bookToSave);
        return bookToSave.getId();
    }

    @Override
    public void updateBook(BookDto bookDto) {
        booksRepo.replace(bookDto.getId(), bookDto);
    }

    @Override
    public Optional<BookDto> findById(String id) {
        return Optional.ofNullable(booksRepo.get(id));
    }

    @Override
    public List<BookDto> findAllActiveBooksByUserId(int page, int elementPerPage, String userId) {
        return filterRepoByUserIdAndBookStatusAndReturnPageWithContent(page, elementPerPage, userId, BookStatusDto.ACTIVE);
    }

    @Override
    public List<BookDto> findAllArchiveBooksByUserId(int page, int elementPerPage, String userId) {
        return filterRepoByUserIdAndBookStatusAndReturnPageWithContent(page, elementPerPage, userId, BookStatusDto.ARCHIVE);
    }


    private List<BookDto> filterRepoByUserIdAndBookStatusAndReturnPageWithContent(int page, int elementPerPage, String userId, BookStatusDto bookStatus) {
        List<BookDto> filteredBooks = booksRepo.values()
                .stream()
                .filter(bookDto -> bookDto.getUserId().equals(userId))
                .filter(bookDto -> bookDto.getBookStatusDto() == bookStatus)
                .collect(Collectors.toList());

        Page<BookDto> bookDtoPage = new PageImpl<>(filteredBooks, PageRequest.of(page, elementPerPage),
                filteredBooks.size());
        return bookDtoPage.getContent();
    }

}

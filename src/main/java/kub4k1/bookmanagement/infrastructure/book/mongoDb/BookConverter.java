package kub4k1.bookmanagement.infrastructure.book.mongoDb;

import kub4k1.bookmanagement.domain.book.dto.BookDto;

class BookConverter {

    BookDocument toDocument(BookDto bookDto){
        return BookDocument.builder()
                .id(bookDto.getId())
                .userId(bookDto.getUserId())
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .bookStatusDto(bookDto.getBookStatusDto())
                .dateOfExpiration(bookDto.getDateOfExpiration())
                .build();
    }

    BookDto toDto(BookDocument bookDocument){
        return BookDto.builder()
                .id(bookDocument.getId())
                .userId(bookDocument.getUserId())
                .title(bookDocument.getTitle())
                .author(bookDocument.getAuthor())
                .bookStatusDto(bookDocument.getBookStatusDto())
                .dateOfExpiration(bookDocument.getDateOfExpiration())
                .build();
    }
}

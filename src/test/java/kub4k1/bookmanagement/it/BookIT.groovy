package kub4k1.bookmanagement.it

import kub4k1.bookmanagement.adapter.incoming.api.ApiEndpoints
import kub4k1.bookmanagement.adapter.incoming.api.ApiResponse
import kub4k1.bookmanagement.adapter.security.SecurityConstants
import kub4k1.bookmanagement.adapter.security.authentication.AuthenticationRequest
import kub4k1.bookmanagement.domain.book.dto.ExtendBookCommand
import kub4k1.bookmanagement.domain.book.dto.NewBookCommand
import kub4k1.bookmanagement.domain.user.dto.RoleDto
import kub4k1.bookmanagement.domain.user.port.outgoing.Encoder
import kub4k1.bookmanagement.infrastructure.book.mongoDb.BookDocument
import kub4k1.bookmanagement.infrastructure.user.mongoDb.UserDocument
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

import java.time.LocalDateTime
import java.time.Month

import static org.springframework.data.mongodb.core.query.Criteria.where

class BookIT extends AbstractIT {

    private String userId

    private NewBookCommand newBookCommand

    HttpEntity<NewBookCommand> newBookCommandHttpEntity

    @Autowired
    Encoder encoder

    def setup() {
        UserDocument userDocument = UserDocument.builder()
                .username("username")
                .email("Kub4k1@gmail.com")
                .password(encoder.encode("S3cr3t_P455w0rd"))
                .active(true)
                .roles(Set.of(RoleDto.USER))
                .build()
        UserDocument savedUser = mongoTemplate.insert(userDocument)
        userId = savedUser.getId()

        newBookCommand = NewBookCommand.builder()
                .title("The Old Man and the Sea")
                .author("Ernest Hemingway")
                .dateOfExpiration(LocalDateTime.of(2020, Month.DECEMBER, 20, 23, 59))
                .userId(userId)
                .build()

        //log in
        HttpEntity<AuthenticationRequest> authenticationRequest = new HttpEntity<>(new AuthenticationRequest("username",
                "S3cr3t_P455w0rd"), headers)
        def result = testRestTemplate.postForEntity(baseUrl + ApiEndpoints.SIGN_IN, authenticationRequest, String.class)
        headers.set("Authorization", result.getHeaders().getFirst(SecurityConstants.ACCESS_TOKEN_HEADER.getConstant()))

        newBookCommandHttpEntity = new HttpEntity<>(newBookCommand, headers)
    }

    def cleanup() {
        mongoTemplate.remove(new Query(where("username").is("username")), UserDocument.class)

        mongoTemplate.remove(new Query(where("title").is(newBookCommand.getTitle())), BookDocument.class)
    }


    def "add new book"() {
        when: "trying to add new book with good parameters"
        def result = testRestTemplate.postForEntity(baseUrl + ApiEndpoints.CREATE_BOOK, newBookCommandHttpEntity,
                ApiResponse.class)

        then: "the system should return 200 with book-id"
        result.getStatusCodeValue() == 200
        StringUtils.isNotEmpty(result.getBody().getContent())
    }

    def "extend book"() {
        given: "added book and extendBookCommand request"
        def result = testRestTemplate.postForEntity(baseUrl + ApiEndpoints.CREATE_BOOK, newBookCommandHttpEntity,
                ApiResponse.class)
        ExtendBookCommand extendBookCommand = new ExtendBookCommand(result.getBody().getContent(),
                newBookCommand.getDateOfExpiration().plusMonths(1))
        HttpEntity<ExtendBookCommand> extendBookCommandHttpEntity = new HttpEntity<>(extendBookCommand, headers)

        when: "trying to extend date of expiration"
        def extendBookResult = testRestTemplate.exchange(baseUrl + ApiEndpoints.EXTEND_BOOK, HttpMethod.PATCH,
                extendBookCommandHttpEntity, ApiResponse.class)

        then: "the system should return 200"
        extendBookResult.getStatusCodeValue() == 200
    }

    def "extend book by earlier date"() {
        given: "added book and extendBookCommand request"
        def result = testRestTemplate.postForEntity(baseUrl + ApiEndpoints.CREATE_BOOK, newBookCommandHttpEntity,
                ApiResponse.class)
        ExtendBookCommand extendBookCommand = new ExtendBookCommand(result.getBody().getContent(),
                newBookCommand.getDateOfExpiration().minusMonths(1))
        HttpEntity<ExtendBookCommand> extendBookCommandHttpEntity = new HttpEntity<>(extendBookCommand, headers)

        when: "trying to extend date of expiration by earlier date"
        def extendBookResult = testRestTemplate.exchange(baseUrl + ApiEndpoints.EXTEND_BOOK, HttpMethod.PATCH,
                extendBookCommandHttpEntity, ApiResponse.class)

        then: "the system should return 400 with message"
        extendBookResult.getStatusCodeValue() == 400
        extendBookResult.getBody().getContent() == "New date of expiration cannot be earlier or the same"
    }

    def "return book"() {
        given: "added book"
        def result = testRestTemplate.postForEntity(baseUrl + ApiEndpoints.CREATE_BOOK, newBookCommandHttpEntity,
                ApiResponse.class)
        HttpEntity<?> returnBookCommandHttpEntity = new HttpEntity<>(null, headers)

        when: "trying to return a book"
        def returnBookResult = testRestTemplate.exchange(
                baseUrl + ApiEndpoints.RETURN_BOOK + "?id=" + result.getBody().getContent(),
                HttpMethod.PATCH, returnBookCommandHttpEntity, ApiResponse.class)

        then: "the system should return 200"
        returnBookResult.getStatusCodeValue() == 200
    }

    def "return returned book"() {
        given: "returned book"
        def result = testRestTemplate.postForEntity(baseUrl + ApiEndpoints.CREATE_BOOK, newBookCommandHttpEntity,
                ApiResponse.class)
        HttpEntity<?> returnBookCommandHttpEntity = new HttpEntity<>(null, headers)
        testRestTemplate.exchange(
                baseUrl + ApiEndpoints.RETURN_BOOK + "?id=" + result.getBody().getContent(),
                HttpMethod.PATCH, returnBookCommandHttpEntity, ApiResponse.class)

        when: "trying to return a book again"
        def returnBookResult = testRestTemplate.exchange(
                baseUrl + ApiEndpoints.RETURN_BOOK + "?id=" + result.getBody().getContent(),
                HttpMethod.PATCH, returnBookCommandHttpEntity, ApiResponse.class)

        then: "the system should return 400 with message"
        returnBookResult.getStatusCodeValue() == 400
        returnBookResult.getBody().getContent() == "The book has already been archived"
    }

    def "get all active books"() {
        given: "added 2 books"
        testRestTemplate.postForEntity(baseUrl + ApiEndpoints.CREATE_BOOK, newBookCommandHttpEntity,
                ApiResponse.class)
        NewBookCommand newBookCommand1 = NewBookCommand.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .dateOfExpiration(LocalDateTime.of(2020, Month.NOVEMBER, 10, 23, 59))
                .userId(userId)
                .build()
        HttpEntity<NewBookCommand> newBookCommandHttpEntity1 = new HttpEntity<>(newBookCommand1, headers)
        testRestTemplate.postForEntity(baseUrl + ApiEndpoints.CREATE_BOOK, newBookCommandHttpEntity1,
                ApiResponse.class)

        when: "trying to get first page of active books by user-id"
        def booksFirstPage = testRestTemplate.exchange(baseUrl + ApiEndpoints.GET_ACTIVE_BOOKS + "?userId=" + userId,
                HttpMethod.GET, new HttpEntity<Object>(headers), List.class)

        then: "the system should return 200 with 2 books"

        booksFirstPage.getStatusCodeValue() == 200
        booksFirstPage.getBody().size() == 2

        cleanup:
        mongoTemplate.remove(new Query(where("title").is(newBookCommand1.getTitle())), BookDocument.class)
    }

}

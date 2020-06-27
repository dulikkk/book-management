package kub4k1.bookmanagement.it

import kub4k1.bookmanagement.adapter.incoming.api.ApiEndpoints
import kub4k1.bookmanagement.adapter.security.SecurityConstants
import kub4k1.bookmanagement.adapter.security.authentication.AuthenticationRequest
import kub4k1.bookmanagement.domain.user.dto.RoleDto
import kub4k1.bookmanagement.domain.user.port.outgoing.Encoder
import kub4k1.bookmanagement.infrastructure.user.mongoDb.UserDocument
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

import static org.springframework.data.mongodb.core.query.Criteria.where

class SecurityIT extends AbstractIT {

    String userId

    String username = "Kub4k1"

    String password = "S3cr3t_P455w0rd"

    String accessToken

    String refreshToken

    @Autowired
    Encoder encoder

    private HttpEntity<AuthenticationRequest> goodAuthenticationRequest = new HttpEntity<>(
            new AuthenticationRequest(username, password), headers)

    def setup() {
        UserDocument userDocument = UserDocument.builder()
                .username(username)
                .email("Kub4k1@gmail.com")
                .password(encoder.encode(password))
                .active(true)
                .roles(Set.of(RoleDto.USER))
                .build()
        UserDocument savedUser = mongoTemplate.insert(userDocument)
        userId = savedUser.getId()
    }

    def cleanup() {
        Query removeQuery = new Query(where("username").is(username))
        mongoTemplate.remove(removeQuery, UserDocument.class)
    }

    def "authenticate user"() {
        when: "trying to authenticate user with good credentials"
        def result = testRestTemplate.postForEntity(baseUrl + ApiEndpoints.SIGN_IN, goodAuthenticationRequest, String.class)

        then: "the system should return 200 with an access token and refresh token"
        result.getStatusCodeValue() == 200
        StringUtils.isNotEmpty(result.getHeaders().getFirst(SecurityConstants.ACCESS_TOKEN_HEADER.getConstant()))
        StringUtils.isNotEmpty(result.getHeaders().getFirst(SecurityConstants.REFRESH_TOKEN_HEADER.getConstant()))
    }

    def "secured endpoint without authentication"() {
        when: "trying to get all books without authentication"
        def result = testRestTemplate.getForEntity(baseUrl + ApiEndpoints.GET_ACTIVE_BOOKS + "?userId=" + userId, String.class)

        then: "the system should return 403"
        result.getStatusCodeValue() == 403
    }

    def "secured endpoint with authentication"() {
        given: "authenticated user and an access token"
        authenticateUserAndExtractAccessTokenAndRefreshToken()
        headers.set(SecurityConstants.ACCESS_TOKEN_HEADER.getConstant(), accessToken)

        when: "trying to get all books with authentication"
        def result = testRestTemplate.exchange(baseUrl + ApiEndpoints.GET_ACTIVE_BOOKS + "?userId=" + userId,
                HttpMethod.GET, new HttpEntity<Object>(headers), List.class)

        then: "the system should return 200"
        result.getStatusCodeValue() == 200
    }

    def "refresh access and refresh token"() {
        given: "authenticated user, access token and refresh token"
        authenticateUserAndExtractAccessTokenAndRefreshToken()
        headers.set(SecurityConstants.REFRESH_TOKEN_HEADER.getConstant(), refreshToken)

        when: "trying to refresh access and refresh token"
        def result = testRestTemplate.exchange(baseUrl + ApiEndpoints.REFRESH_TOKENS,
                HttpMethod.GET, new HttpEntity<Object>(headers), String.class)

        then: "system should return 200 with new access and refresh token"
        result.getStatusCodeValue() == 200
        StringUtils.isNotEmpty(result.getHeaders().getFirst(SecurityConstants.ACCESS_TOKEN_HEADER.getConstant()))
        StringUtils.isNotEmpty(result.getHeaders().getFirst(SecurityConstants.REFRESH_TOKEN_HEADER.getConstant()))
    }

    def "log out"(){

    }

    def authenticateUserAndExtractAccessTokenAndRefreshToken() {
        def result = testRestTemplate.postForEntity(baseUrl + ApiEndpoints.SIGN_IN, goodSignInRequest, String.class)

        accessToken = result.getHeaders().getFirst(SecurityConstants.ACCESS_TOKEN_HEADER.getConstant())
        refreshToken = result.getHeaders().getFirst(SecurityConstants.REFRESH_TOKEN_HEADER.getConstant())
    }

}

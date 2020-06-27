package kub4k1.bookmanagement.it

import kub4k1.bookmanagement.adapter.incoming.api.ApiEndpoints
import kub4k1.bookmanagement.adapter.incoming.api.ApiResponse
import kub4k1.bookmanagement.domain.user.dto.NewUserCommand
import kub4k1.bookmanagement.infrastructure.user.mongoDb.UserDocument
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpEntity

import static org.springframework.data.mongodb.core.query.Criteria.where

class UserIT extends AbstractIT {

    private NewUserCommand goodNewUserCommand = new NewUserCommand("Kub4k1", "Kub4k1@gmail.com", "S3cr3t_P455w0rd")

    private HttpEntity<NewUserCommand> goodSignUpRequest = new HttpEntity<>(goodNewUserCommand, headers)

    private NewUserCommand badNewUserCommand = new NewUserCommand("ok", "Kub4k1gmail.com", "Secret_Password")

    private HttpEntity<NewUserCommand> badSignUpRequest = new HttpEntity<>(badNewUserCommand, headers)

    def cleanup() {
        Query removeQuery = new Query(where("username").is(goodNewUserCommand.getUsername()))
        mongoTemplate.remove(removeQuery, UserDocument.class)
    }

    def "sign up with good parameters"() {
        when: "trying to register with good parameters"
        def result = testRestTemplate.postForEntity(baseUrl + ApiEndpoints.SIGN_UP, goodSignUpRequest, ApiResponse.class)

        then: "the system should return 201"
        result.getStatusCodeValue() == 201
    }

    def "sign up with bad parameters"() {
        when: "trying to register with bad parameters"
        def result = testRestTemplate.postForEntity(baseUrl + ApiEndpoints.SIGN_UP, badSignUpRequest, ApiResponse.class)

        then: "the system should return 400"
        result.getStatusCodeValue() == 400
    }

    def "activate new user"() {
        given: "registered user and his activation token"
        testRestTemplate.postForEntity(baseUrl + ApiEndpoints.SIGN_UP, goodSignUpRequest, ApiResponse.class)
        String activationToken = extractActivationToken()

        when: "trying to activate his account with good token"
        def result = testRestTemplate.getForEntity(baseUrl + ApiEndpoints.USER_ACTIVATION + "?token=" + activationToken, String.class)

        then: "the system should return 200"
        result.getStatusCodeValue() == 200

    }

    def "activate new user with bad token"() {
        given: "registered user and his bad activation token"
        testRestTemplate.postForEntity(baseUrl + ApiEndpoints.SIGN_UP, goodNewUserCommand, ApiResponse.class)
        String badActivationToken = "abc123"

        when: "trying to activate his account with good token"
        def result = testRestTemplate.getForEntity(baseUrl + ApiEndpoints.USER_ACTIVATION + "?token=" + badActivationToken, String.class)

        then: "the system should return 400"
        result.getStatusCodeValue() == 400
    }

    def extractActivationToken() {
        Query usernameQuery = new Query(where("username").is(goodNewUserCommand.getUsername()))
        UserDocument userDocument = mongoTemplate.findOne(usernameQuery, UserDocument.class)
        return userDocument.getActivationToken()
    }
}

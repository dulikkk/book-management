package kub4k1.bookmanagement.domain.user

import kub4k1.bookmanagement.domain.user.dto.NewUserCommand
import kub4k1.bookmanagement.domain.user.dto.exception.UserException
import kub4k1.bookmanagement.domain.user.port.outgoing.ActivationTokenCreator
import kub4k1.bookmanagement.domain.user.port.outgoing.Encoder
import kub4k1.bookmanagement.domain.user.port.outgoing.TokenSender
import kub4k1.bookmanagement.infrastructure.user.memory.InMemoryUserRepository
import spock.lang.Specification

import java.lang.reflect.Field
import java.time.LocalDateTime

class UserDomainTest extends Specification {

    private InMemoryUserRepository inMemoryUserRepository = new InMemoryUserRepository()

    private Encoder encoder = Mock(Encoder)

    private ActivationTokenCreator activationTokenCreator = Mock(ActivationTokenCreator)

    private TokenSender tokenSender = Mock(TokenSender)

    private UserDomainFacade userFacade = new UserDomainConfigurator().userDomainFacade(inMemoryUserRepository, encoder,
            activationTokenCreator, tokenSender)

    private NewUserCommand newUserCommand = new NewUserCommand("Kub4k1", "Kub4k1@gmail.com", "S3cr3t_P455w0rd")

    def setup() {
        encoder.encode() >> ""
        activationTokenCreator.generateToken() >> "abc123"
        activationTokenCreator.getExpirationDateFromToken("abc123") >> LocalDateTime.now().plusHours(1)
    }

    def "add new user"() {
        when: "add new user"
        String userId = userFacade.addNewUser(newUserCommand)

        then: "the system should create new disabled user"
        inMemoryUserRepository.findById(userId).isPresent()
        !inMemoryUserRepository.findById(userId).get().active
    }

    def "add exists user"() {
        given: "added user"
        userFacade.addNewUser(newUserCommand)

        when: "trying register with the same username and email"
        userFacade.addNewUser(newUserCommand)

        then: "the system should throw exception"
        thrown(UserException)
    }

    def "add user with bad email"() {
        given: "user with bad email"
        NewUserCommand badEmailNewUserCommand = new NewUserCommand("Kub4k1", email, "S3cr3t_P455w0rd")

        when: "trying to register with bad email"
        userFacade.addNewUser(badEmailNewUserCommand)


        then: "the system should throw an exception"
        def exception = thrown(expectedException)
        exception.message == expectedMessage

        where:
        email             || expectedException || expectedMessage
        null              || UserException     || "Incorrect email"
        ""                || UserException     || "Incorrect email"
        "kub4k1gmail.com" || UserException     || "Incorrect email"

    }

    def "add the user with bad password"() {
        given: "user with bad password"
        NewUserCommand badPasswordNewUserCommand = new NewUserCommand("ark21", "Kub4k1@gmail.com", password)

        when: "trying to register with bad password"
        userFacade.addNewUser(badPasswordNewUserCommand)

        then: "the system should throw an exception"
        def exception = thrown(expectedException)
        exception.message == expectedMessage

        where:
        password        || expectedException || expectedMessage
        null            || UserException     || "Password cannot be null or empty"
        ""              || UserException     || "Password cannot be null or empty"
        "tooshort"      || UserException     || "Password must be at least 10 characters and one number"
        "withoutnumber" || UserException     || "Password must be at least 10 characters and one number"

    }

    def "activate user"() {
        given: "registered user and his token"
        String userId = userFacade.addNewUser(newUserCommand)
        String token = extractTokenByUserId(userId)

        when: "trying to activate account with good token"
        userFacade.activateUser(token)

        then: "system should activate this user"
        inMemoryUserRepository.findById(userId).get().isActive()
    }

    def "activate user with bad token"() {
        given: "registered user and bad token"
        userFacade.addNewUser(newUserCommand)
        String badToken = "abc123"

        when: "trying to activate account with bad token"
        userFacade.activateUser(badToken)

        then: "the system should throw an exception"
        thrown(UserException)
    }

    String extractTokenByUserId(String userId) {
        Field tokensRepoField = InMemoryUserRepository.class.getDeclaredField("tokensRepo")
        tokensRepoField.setAccessible(true)
        Map<String, String> tokensRepo = tokensRepoField.get(inMemoryUserRepository) as Map<String, String>
        return tokensRepo.get(userId)
    }

}

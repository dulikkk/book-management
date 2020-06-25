package kub4k1.bookmanagement.domain.user;

import kub4k1.bookmanagement.domain.user.port.outgoing.ActivationTokenCreator;
import kub4k1.bookmanagement.domain.user.port.outgoing.Encoder;
import kub4k1.bookmanagement.domain.user.port.outgoing.TokenSender;
import kub4k1.bookmanagement.domain.user.port.outgoing.UserRepository;
import kub4k1.bookmanagement.domain.user.query.UserQueryRepository;
import kub4k1.bookmanagement.infrastructure.user.memory.InMemoryUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDomainConfigurator {

    public UserDomainFacade userDomainFacade(InMemoryUserRepository inMemoryUserRepository, Encoder encoder,
                                 ActivationTokenCreator activationTokenCreator, TokenSender tokenSender) {
        return new UserDomainFacade(inMemoryUserRepository, inMemoryUserRepository, encoder, tokenSender,
                activationTokenCreator);
    }

    @Bean
    UserDomainFacade userDomainFacade(UserRepository userRepository, UserQueryRepository userQueryRepository, Encoder encoder,
                                ActivationTokenCreator activationTokenCreator, TokenSender tokenSender) {
        return new UserDomainFacade(userRepository, userQueryRepository, encoder, tokenSender, activationTokenCreator);
    }
}

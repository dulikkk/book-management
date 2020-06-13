package kub4k1.bookmanagement.domain.user;

import kub4k1.bookmanagement.domain.user.port.outgoing.ActivationTokenCreator;
import kub4k1.bookmanagement.domain.user.port.outgoing.Encoder;
import kub4k1.bookmanagement.domain.user.port.outgoing.TokenSender;
import kub4k1.bookmanagement.infrastructure.user.memory.InMemoryUserRepository;

public class UserDomainConfigurator {

    public UserDomainFacade userFacade(InMemoryUserRepository inMemoryUserRepository, Encoder encoder,
                                 ActivationTokenCreator activationTokenCreator, TokenSender tokenSender) {
        return new UserDomainFacade(inMemoryUserRepository, inMemoryUserRepository, encoder, tokenSender,
                activationTokenCreator);
    }
}

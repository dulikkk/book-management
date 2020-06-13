package kub4k1.bookmanagement.domain.user;

import kub4k1.bookmanagement.domain.user.dto.NewUserCommand;
import kub4k1.bookmanagement.domain.user.port.outgoing.ActivationTokenCreator;
import kub4k1.bookmanagement.domain.user.port.outgoing.Encoder;
import kub4k1.bookmanagement.domain.user.port.outgoing.TokenSender;
import kub4k1.bookmanagement.domain.user.port.outgoing.UserRepository;
import kub4k1.bookmanagement.domain.user.query.UserQueryRepository;

public class UserDomainFacade {

    private UserActivator userActivator;
    private UserCreator userCreator;

    public UserDomainFacade(UserRepository userRepository, UserQueryRepository userQueryRepository, Encoder encoder,
                            TokenSender tokenSender, ActivationTokenCreator activationTokenCreator) {
        this.userActivator = new UserActivator(tokenSender, activationTokenCreator, userRepository, userQueryRepository);
        this.userCreator = new UserCreator(userRepository, encoder, userActivator, new UserValidator(userQueryRepository));
    }

    public String addNewUser(NewUserCommand newUserCommand) {
        return userCreator.createUser(newUserCommand);
    }

    public void activateUser(String token) {
        userActivator.validateTokenAndActivateUser(token);
    }
}

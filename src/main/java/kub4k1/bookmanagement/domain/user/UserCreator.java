package kub4k1.bookmanagement.domain.user;

import kub4k1.bookmanagement.domain.user.dto.NewUserCommand;
import kub4k1.bookmanagement.domain.user.dto.RoleDto;
import kub4k1.bookmanagement.domain.user.dto.UserDto;
import kub4k1.bookmanagement.domain.user.port.outgoing.Encoder;
import kub4k1.bookmanagement.domain.user.port.outgoing.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
class UserCreator {

    private final UserRepository userRepository;
    private final Encoder encoder;
    private final UserActivator userActivator;
    private final UserValidator userValidator;

    String createUser(NewUserCommand newUserCommand) {
        userValidator.validateNewUser(newUserCommand);

        String encodedPassword = encoder.encode(newUserCommand.getPassword());

        UserDto newUserDtoToSave = UserDto.builder()
                .username(newUserCommand.getUsername())
                .email(newUserCommand.getEmail())
                .password(encodedPassword)
                .roles(Set.of(RoleDto.USER))
                .active(false)
                .build();

        UserDto savedUser = userRepository.createUser(newUserDtoToSave);

        new Thread(() -> userActivator.createAndSendActivationToken(savedUser.getId(), savedUser.getEmail())).start();

        return savedUser.getId();
    }
}

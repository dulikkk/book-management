package kub4k1.bookmanagement.domain.user;

import kub4k1.bookmanagement.domain.user.dto.UserDto;
import kub4k1.bookmanagement.domain.user.dto.exception.UserException;
import kub4k1.bookmanagement.domain.user.port.outgoing.ActivationTokenCreator;
import kub4k1.bookmanagement.domain.user.port.outgoing.TokenSender;
import kub4k1.bookmanagement.domain.user.port.outgoing.UserRepository;
import kub4k1.bookmanagement.domain.user.query.UserQueryRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
class UserActivator {

    private final TokenSender tokenSender;
    private final ActivationTokenCreator activationTokenCreator;
    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;

    void createAndSendActivationToken(String userId, String receiver) {
        String activationToken = activationTokenCreator.generateToken();
        userRepository.saveActivationToken(activationToken, userId);
        tokenSender.sendToken(activationToken, receiver);
    }

    void validateTokenAndActivateUser(String token) {

        if (!userRepository.isTokenExists(token)) {
            throw new UserException("This token does not exist. Please contact with our tech support to solve this problem");
        }

        UserDto userFromActivationToken = userQueryRepository.getUserFromActivationToken(token);

        LocalDateTime tokenExpirationDate = activationTokenCreator.getExpirationDateFromToken(token);
        if (!isGoodDate(tokenExpirationDate)) {
            resendToken(userFromActivationToken);
            throw new UserException("This activation link is outdated. We sent to your email new token");
        } else {
            activateUser(userFromActivationToken);
        }
    }


    private void activateUser(UserDto userToActivate) {
        UserDto activatedUser = UserDto.builder()
                .id(userToActivate.getId())
                .username(userToActivate.getUsername())
                .active(true)
                .email(userToActivate.getEmail())
                .roles(userToActivate.getRoles())
                .password(userToActivate.getPassword())
                .build();

        userRepository.activateUser(activatedUser);
    }

    private boolean isGoodDate(LocalDateTime expirationDate) {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expirationDate);
    }

    private void resendToken(UserDto receiver) {
        String newActivationToken = activationTokenCreator.generateToken();
        userRepository.restoreActivationToken(newActivationToken, receiver.getId());
        tokenSender.sendToken(newActivationToken, receiver.getEmail());
    }
}

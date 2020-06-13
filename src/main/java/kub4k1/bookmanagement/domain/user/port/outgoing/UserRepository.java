package kub4k1.bookmanagement.domain.user.port.outgoing;

import kub4k1.bookmanagement.domain.user.dto.UserDto;

public interface UserRepository {

    UserDto createUser(UserDto userDto);

    void activateUser(UserDto userToUpdate);

    void saveActivationToken(String token, String userId);

    boolean isTokenExists(String token);

    void restoreActivationToken(String newToken, String userId);
}

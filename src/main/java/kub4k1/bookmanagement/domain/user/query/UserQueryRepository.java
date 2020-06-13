package kub4k1.bookmanagement.domain.user.query;

import kub4k1.bookmanagement.domain.user.dto.UserDto;

import java.util.Optional;

public interface UserQueryRepository {

    Optional<UserDto> findByUsername(String username);

    Optional<UserDto> findByEmail(String email);

    Optional<UserDto> findById(String id);

    UserDto getUserFromActivationToken(String token);
}

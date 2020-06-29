package kub4k1.bookmanagement.adapter.security.securityToken;

import kub4k1.bookmanagement.domain.user.dto.UserDto;

import java.util.Optional;

public interface RefreshTokenRepository {

    void saveRefreshToken(String refreshToken, String username);

    void deleteRefreshToken(String refreshToken);

    Optional<UserDto> getUserByRefreshToken(String refreshToken);
}

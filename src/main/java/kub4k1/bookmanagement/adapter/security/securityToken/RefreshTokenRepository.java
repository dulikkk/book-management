package kub4k1.bookmanagement.adapter.security.securityToken;

import kub4k1.bookmanagement.domain.user.dto.UserDto;

public interface RefreshTokenRepository {

    void saveRefreshToken(String refreshToken, String username);

    boolean isRefreshTokenExist(String refreshToken);

    void deleteRefreshToken(String refreshToken);

    UserDto getUserByRefreshToken(String refreshToken);
}

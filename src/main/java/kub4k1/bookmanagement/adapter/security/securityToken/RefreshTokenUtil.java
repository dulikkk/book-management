package kub4k1.bookmanagement.adapter.security.securityToken;

import kub4k1.bookmanagement.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;

@RequiredArgsConstructor
@Component
public class RefreshTokenUtil {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AccessTokenUtil accessTokenUtil = new AccessTokenUtil();

    public String generateRefreshToken(String username) {
        String refreshToken = randomUUID().toString();
        refreshTokenRepository.saveRefreshToken(refreshToken, username);

        return refreshToken;
    }

    public SecurityTokenPair refreshAccessAndRefreshToken(String refreshToken) {
        if (!refreshTokenRepository.isRefreshTokenExist(refreshToken)) {
            throw new SecurityException("This refresh token does not exists");
        }
        UserDto userFromRefreshToken = refreshTokenRepository.getUserByRefreshToken(refreshToken);

        deleteRefreshToken(refreshToken);

        List<String> roles = userFromRefreshToken.getRoles()
                .stream()
                .map(Enum::toString)
                .collect(Collectors.toUnmodifiableList());

        String newAccessToken = accessTokenUtil.generateAccessToken(userFromRefreshToken.getId(), roles);
        String newRefreshToken = generateRefreshToken(userFromRefreshToken.getUsername());

        return new SecurityTokenPair(newAccessToken, newRefreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteRefreshToken(refreshToken);
    }

}

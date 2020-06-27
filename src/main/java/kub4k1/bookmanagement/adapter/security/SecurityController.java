package kub4k1.bookmanagement.adapter.security;

import kub4k1.bookmanagement.adapter.incoming.api.ApiEndpoints;
import kub4k1.bookmanagement.adapter.incoming.api.ApiResponse;
import kub4k1.bookmanagement.adapter.security.securityToken.AccessTokenUtil;
import kub4k1.bookmanagement.adapter.security.securityToken.RefreshTokenUtil;
import kub4k1.bookmanagement.adapter.security.securityToken.SecurityTokenPair;
import kub4k1.bookmanagement.domain.user.UserDomainFacade;
import kub4k1.bookmanagement.domain.user.dto.NewUserCommand;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.time.LocalDateTime.now;

@RequiredArgsConstructor
@RestController
class SecurityController {

    private final UserDomainFacade userDomainFacade;
    private final RefreshTokenUtil refreshTokenUtil;

    @PostMapping(ApiEndpoints.SIGN_UP)
    ResponseEntity<ApiResponse> register(@RequestBody NewUserCommand newUserCommand) {
        String userId = userDomainFacade.addNewUser(newUserCommand);

        ApiResponse apiResponse = ApiResponse.builder()
                .content("You have registered successfully. We are sending to your email activation link")
                .content(userId)
                .status(HttpStatus.CREATED.value())
                .timestamp(now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping(ApiEndpoints.USER_ACTIVATION)
    ResponseEntity<ApiResponse> verifyActivationToken(@RequestParam String token) {
        userDomainFacade.activateUser(token);

        ApiResponse apiResponse = ApiResponse.builder()
                .content("Your account has been activated successfully")
                .status(HttpStatus.OK.value())
                .timestamp(now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping(ApiEndpoints.REFRESH_TOKENS)
    void refreshTokens(HttpServletRequest req, HttpServletResponse res) {
        String refreshToken = req.getHeader(SecurityConstants.REFRESH_TOKEN_HEADER.getConstant());

        if (StringUtils.isNotEmpty(refreshToken)) {
            SecurityTokenPair tokens = refreshTokenUtil.refreshAccessAndRefreshToken(refreshToken);

            res.addHeader(SecurityConstants.ACCESS_TOKEN_HEADER.getConstant(),
                    SecurityConstants.TOKEN_PREFIX.getConstant() + tokens.getAccessToken());

            res.addHeader(SecurityConstants.REFRESH_TOKEN_HEADER.getConstant(), tokens.getRefreshToken());
        } else {
            throw new SecurityException("Cannot refresh tokens. Reason: Bad refresh token");
        }
    }

    @PostMapping(ApiEndpoints.LOG_OUT)
    ResponseEntity logOut(HttpServletRequest req) {
        if (StringUtils.isNotEmpty(req.getHeader(SecurityConstants.REFRESH_TOKEN_HEADER.getConstant()))) {
            refreshTokenUtil.deleteRefreshToken(req.getHeader(SecurityConstants.REFRESH_TOKEN_HEADER.getConstant()));
            return ResponseEntity.ok().build();
        } else {
            throw new SecurityException("Cannot log out. Reason: Bad refresh token");
        }

    }
}

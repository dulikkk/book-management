package kub4k1.bookmanagement.adapter.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import kub4k1.bookmanagement.adapter.incoming.api.ApiEndpoints;
import kub4k1.bookmanagement.adapter.security.SecurityConstants;
import kub4k1.bookmanagement.adapter.security.securityToken.AccessTokenUtil;
import kub4k1.bookmanagement.adapter.security.securityToken.RefreshTokenUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenUtil refreshTokenUtil;
    private final AccessTokenUtil accessTokenUtil = new AccessTokenUtil();

    public AuthenticationFilter(AuthenticationManager authenticationManager, RefreshTokenUtil refreshTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.refreshTokenUtil = refreshTokenUtil;
        setFilterProcessesUrl(ApiEndpoints.SIGN_IN);
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            AuthenticationRequest signInRequest = new ObjectMapper().readValue(request.getInputStream(),
                    AuthenticationRequest.class);

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword());

            return authenticationManager.authenticate(authRequest);
        } catch (IOException e) {
            throw new SecurityException(e);
        }


    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        User authUser = (User) auth.getPrincipal();

        List<String> roles = authUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toUnmodifiableList());

        String accessToken = accessTokenUtil.generateAccessToken(authUser.getUsername(), roles);
        String refreshToken = refreshTokenUtil.generateRefreshToken(authUser.getUsername());

        res.addHeader(SecurityConstants.ACCESS_TOKEN_HEADER.getConstant(), SecurityConstants.TOKEN_PREFIX.getConstant() + accessToken);
        res.addHeader(SecurityConstants.REFRESH_TOKEN_HEADER.getConstant(), refreshToken);

    }
}

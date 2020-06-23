package kub4k1.bookmanagement.adapter.security.authorization;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import kub4k1.bookmanagement.adapter.security.SecurityConstants;
import kub4k1.bookmanagement.adapter.security.securityToken.AccessTokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final AccessTokenUtil accessTokenUtil = new AccessTokenUtil();

    public AuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        getAuthentication(httpServletRequest)
                .ifPresent(auth -> SecurityContextHolder.getContext().setAuthentication(auth)
                );

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private Optional<UsernamePasswordAuthenticationToken> getAuthentication(HttpServletRequest req) {
        String token = req.getHeader(SecurityConstants.ACCESS_TOKEN_HEADER.getConstant());

        if (StringUtils.isNotEmpty(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX.getConstant())) {

            Jws<Claims> parsedToken = accessTokenUtil.parseAccessToken(
                    token.replace(SecurityConstants.TOKEN_PREFIX.getConstant(), ""));

            String username = accessTokenUtil.getUsernameFromAccessToken(parsedToken);
            List<String> roles = accessTokenUtil.getAuthoritiesFromJwt(parsedToken);

            List<GrantedAuthority> authorities = roles
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toUnmodifiableList());

            if (StringUtils.isNotEmpty(username)) {
                return Optional.of(new UsernamePasswordAuthenticationToken(username, null, authorities));
            }
        }
        return Optional.empty();
    }
}

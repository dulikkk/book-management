package kub4k1.bookmanagement.adapter.security.securityToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kub4k1.bookmanagement.adapter.security.SecurityConstants;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccessTokenUtil {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final int EXPIRATION_TIME_IN_SECOND = 10 * 60 * 1000; // 10 minutes

    public String generateAccessToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_SECOND))
                .setAudience(SecurityConstants.TOKEN_AUDIENCE.getConstant())
                .setIssuer(SecurityConstants.TOKEN_ISSUER.getConstant())
                .claim("roles", roles)
                .signWith(SECRET_KEY)
                .compact();
    }

    public Jws<Claims> parseAccessToken(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(jwt);
    }

    public String getUsernameFromAccessToken(Jws<Claims> claims) {
        return claims.getBody()
                .getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getAuthoritiesFromJwt(Jws<Claims> claims) {
        return claims.getBody().get("roles", ArrayList.class);
    }
}

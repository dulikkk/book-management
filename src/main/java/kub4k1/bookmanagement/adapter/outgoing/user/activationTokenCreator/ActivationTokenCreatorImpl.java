package kub4k1.bookmanagement.adapter.outgoing.user.activationTokenCreator;

import kub4k1.bookmanagement.domain.user.port.outgoing.ActivationTokenCreator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.UUID.randomUUID;

@Service
class ActivationTokenCreatorImpl implements ActivationTokenCreator {

    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final static String TOKEN_SEPARATOR = "_";

    @Override
    // Activation token has expiration time and random UUID.
    // expirationDate{TOKEN_SEPARATOR}UUID
    public String generateToken() {
        StringBuilder token = new StringBuilder();

        //expiration time
        LocalDateTime tokenExpiration = LocalDateTime.now().plusDays(1);
        token.append(tokenExpiration.format(DATE_TIME_FORMATTER));
        token.append(TOKEN_SEPARATOR);

        //random UUID
        token.append(randomUUID());


        return token.toString();
    }


    @Override
    public LocalDateTime getExpirationDateFromToken(String token) {
        int expirationDateEnd = token.indexOf(TOKEN_SEPARATOR);
        String expirationDate = token.substring(0, expirationDateEnd);

        return LocalDateTime.parse(expirationDate, DATE_TIME_FORMATTER);

    }
}

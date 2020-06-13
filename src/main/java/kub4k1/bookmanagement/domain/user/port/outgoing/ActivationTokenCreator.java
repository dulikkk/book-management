package kub4k1.bookmanagement.domain.user.port.outgoing;

import java.time.LocalDateTime;

public interface ActivationTokenCreator {

    String generateToken();

    LocalDateTime getExpirationDateFromToken(String token);
}

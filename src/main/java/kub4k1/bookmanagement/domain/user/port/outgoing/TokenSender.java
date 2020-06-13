package kub4k1.bookmanagement.domain.user.port.outgoing;

public interface TokenSender {

    void sendToken(String token, String email);
}

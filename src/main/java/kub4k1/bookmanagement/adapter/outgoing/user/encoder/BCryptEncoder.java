package kub4k1.bookmanagement.adapter.outgoing.user.encoder;

import kub4k1.bookmanagement.domain.user.port.outgoing.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
class BCryptEncoder implements Encoder {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String textToEncode) {
        return passwordEncoder.encode(textToEncode);
    }
}

package kub4k1.bookmanagement.adapter.security.authentication;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class AuthenticationRequest {

    String username;

    String password;
}

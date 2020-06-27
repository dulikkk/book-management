package kub4k1.bookmanagement.domain.user.dto;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class NewUserCommand {

    String username;

    String email;

    String password;

}

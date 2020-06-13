package kub4k1.bookmanagement.domain.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Getter
@Value
@RequiredArgsConstructor
public class NewUserCommand {

    String username;

    String email;

    String password;

}

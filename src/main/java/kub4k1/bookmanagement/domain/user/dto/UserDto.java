package kub4k1.bookmanagement.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.util.Set;

@Builder
@Getter
@Value
public class UserDto {

    String id;

    String username;

    String email;

    String password;

    Set<RoleDto> roles;

    boolean active;
}

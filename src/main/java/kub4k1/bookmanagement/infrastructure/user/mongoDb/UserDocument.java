package kub4k1.bookmanagement.infrastructure.user.mongoDb;

import kub4k1.bookmanagement.domain.user.dto.RoleDto;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Getter
@Builder
@Document("user")
class UserDocument {

    @Id
    private String id;

    private final String username;

    private final String email;

    private final String password;

    private final Set<RoleDto> roles;

    private final boolean active;

    private final String activationToken;

    private final String refreshToken;

}

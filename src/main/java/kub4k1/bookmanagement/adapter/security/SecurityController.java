package kub4k1.bookmanagement.adapter.security;

import kub4k1.bookmanagement.adapter.incoming.api.ApiEndpoints;
import kub4k1.bookmanagement.adapter.incoming.api.ApiResponse;
import kub4k1.bookmanagement.domain.user.UserDomainFacade;
import kub4k1.bookmanagement.domain.user.dto.NewUserCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import static java.time.LocalDateTime.now;

@RequiredArgsConstructor
class SecurityController {

    private final UserDomainFacade userDomainFacade;

    @PostMapping(ApiEndpoints.SIGN_UP)
    ResponseEntity<ApiResponse> register(@RequestBody NewUserCommand newUserCommand) {
        String userId = userDomainFacade.addNewUser(newUserCommand);

        ApiResponse apiResponse = ApiResponse.builder()
                .content("You have registered successfully. We are sending to your email activation link")
                .content(userId)
                .status(HttpStatus.CREATED.value())
                .timestamp(now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping(ApiEndpoints.USER_ACTIVATION)
    ResponseEntity<ApiResponse> verifyActivationToken(@RequestParam String token) {
        userDomainFacade.activateUser(token);

        ApiResponse apiResponse = ApiResponse.builder()
                .content("Your account has been activated successfully")
                .status(HttpStatus.OK.value())
                .timestamp(now())
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}

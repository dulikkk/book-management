package kub4k1.bookmanagement.domain.user;

import kub4k1.bookmanagement.domain.user.dto.NewUserCommand;
import kub4k1.bookmanagement.domain.user.dto.exception.UserException;
import kub4k1.bookmanagement.domain.user.query.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Pattern;

@RequiredArgsConstructor
class UserValidator {

    private final UserQueryRepository userQueryRepository;
    private final EmailValidator emailValidator = EmailValidator.getInstance();
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9]).{10,}$");

    public void validateNewUser(NewUserCommand newUserCommand) {
        validateFields(newUserCommand);

        checkIfThisUserExists(newUserCommand);
    }

    private void validateFields(NewUserCommand newUserCommand) {

        if (StringUtils.isEmpty(newUserCommand.getUsername())) {
            throw new UserException("Username cannot be null or empty");
        }

        if (!emailValidator.isValid(newUserCommand.getEmail())) {
            throw new UserException("Incorrect email");
        }

        if(StringUtils.isEmpty(newUserCommand.getPassword())){
            throw new UserException("Password cannot be null or empty");
        }

        if (!PASSWORD_PATTERN.matcher(newUserCommand.getPassword()).matches()) {
            throw new UserException("Password must be at least 10 characters and one number");
        }
    }

    private void checkIfThisUserExists(NewUserCommand newUserCommand) {
        userQueryRepository.findByEmail(newUserCommand.getEmail())
                .ifPresent(fetchedUser -> {
                    throw new UserException("User with email " + newUserCommand.getEmail() + " has already exist");
                });
        userQueryRepository.findByUsername(newUserCommand.getUsername())
                .ifPresent(fetchedUser -> {
                    throw new UserException(
                            "User with username " + newUserCommand.getUsername() + " has already exist");
                });
    }
}

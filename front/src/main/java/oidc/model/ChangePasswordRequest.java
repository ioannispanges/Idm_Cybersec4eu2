package oidc.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A container for a login request
 */
@Getter
@Setter
public class ChangePasswordRequest {

    private String username;
    private String oldPassword;

    @NotNull(message = "Can't be empty")
    @Size(min = 4, message = "Must have more than 4 symbols")
    private String newPassword;

    private String passwordCheck;
    private boolean passwordsEqual;


    @AssertTrue(message = "Passwords should match")
    public boolean isPasswordsEqual() {
        if(newPassword != null){
            return newPassword.equals(passwordCheck);
        }
        return false;
    }
}

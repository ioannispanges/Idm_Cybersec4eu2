package oidc.model;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * A container for a delete account request
 */
@Getter
@Setter
public class DeleteAccountRequest {
    @NotNull(message = "Can't be empty")
    private String username;
    @NotNull(message = "Can't be empty")
    private String password;

    private String passwordCheck;
    private boolean passwordsEqual;


    @AssertTrue(message = "Passwords should match")
    public boolean isPasswordsEqual() {
        if(password != null){
            return password.equals(passwordCheck);
        }
        return false;
    }
}

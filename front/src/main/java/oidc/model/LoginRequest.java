package oidc.model;

/**
 * A container for a login request
 */
public class LoginRequest {

    private String username;
    private String password;

    public String getPassword() {
		return password;
	}

    public void setPassword(String password) {
		this.password = password;
	}

    public String getUsername() {
		return username;
	}

    public void setUsername(String username) {
		this.username = username;
	}
}

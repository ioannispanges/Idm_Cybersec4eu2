package oidc.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MainRequest {
    @NotNull(message = "Can't be empty")
    @Size(min = 1, message = "Must not be empty")
    private String username;
    public String getUsername() {
        return username;
    }

    @NotNull(message = "Can't be empty")
    @Size(min = 1, message = "Must not be empty")
    private String firstName;
    public String getFirstName() {
        return firstName;
    }

    @NotNull(message = "Can't be empty")
    @Size(min = 1, message = "Must not be empty")
    private String lastName;
    public String getLastName() {
        return lastName;
    }

    @NotNull(message = "Can't be empty")
    @Size(min = 1, message = "Must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String birthdate;

    @NotNull(message = "Can't be empty")
    @Size(min = 1, message = "Must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String awardeddegree;

    @NotNull(message = "Can't be empty")
    @Size(min = 1, message = "Must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String studentid;

    @NotNull(message = "Can't be empty")
    @Size(min = 1, message = "Must not be empty")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String university;
}
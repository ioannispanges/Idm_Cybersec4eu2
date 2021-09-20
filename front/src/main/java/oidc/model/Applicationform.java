package oidc.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Applicationform {

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String applicationcategory;

    @NotNull(message = "Can't be empty")
    @Size(min = 4, message = "Must have more than 4 symbols")
    private String academicProgramme;


    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String proposedTopic;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String proposedSupervisor;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String proposedGroup;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String firstName;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String lastName;

//    @NotNull(message = "Can't be empty")
//    @Size(min=1, message = "Must not be empty")
//    private String email;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String gender;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String email;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String address1;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String address2;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String city;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String country;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String postalcode;


    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String departmentname;

    @NotNull(message = "Can't be empty")
    @Size(min=1, message = "Must not be empty")
    private String universityname;


}

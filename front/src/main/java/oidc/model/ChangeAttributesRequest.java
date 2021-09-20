package oidc.model;


import lombok.Getter;
import lombok.Setter;

/**
 * A container for a login request
 */
@Getter
@Setter
public class ChangeAttributesRequest implements AttributeContainer {
//    @NotNull(message = "Can't be empty")
//    @Size(min=1, message = "Must not be empty")
//    private String  proposedSupervisor;;
//    @NotNull(message = "Can't be empty")
//    @Size(min=1, message = "Must not be empty")
//    private String applicationcategory;
//    @NotNull(message = "Can't be empty")
//    @Size(min=1, message = "Must not be empty")
//    private String academicprogramme;
//    @NotNull(message = "Can't be empty")
//    @Size(min=1, message = "Must not be empty")
//    private String name;
//    @NotNull(message = "Can't be empty")
//    @Size(min=1, message = "Must not be empty")
//    private String address;
//    @NotNull(message = "Can't be empty")
//    @Size(min=1, message = "Must not be empty")
//    private String city;
//    @NotNull(message = "Can't be empty")
//    @Size(min=1, message = "Must not be empty")
//    private String country;
//    @NotNull(message = "Can't be empty")
//    @Size(min=1, message = "Must not be empty")
//    private String departmentname;
//
//
////    @DateTimeFormat(pattern = "yyyy-MM-dd")
////    private String birthdate;
//
//
//    @NotNull(message = "Can't be empty")
//    @Size(min=1, message = "Must not be empty")
//    private String university;
//
//
//}

    private String applicationcategory;
    private String applicationentry;
    private String academicprogramme;
    private String modestudy;
    private String proposedtopic;
    private String proposedsupervisor;
    private String proposedgroup;
    private String firstname;
    private String lastname;
    private String gender;
    private String email;
    private String address1;
    private String address2;
    private String city;
    private String country;
    private String postalcode;
    private String departmentname;
    private String universityname;
    private String organizationcountry;
    private String additionalinstitutions;
    private String schoolname;
    private String schoolCountry;


}

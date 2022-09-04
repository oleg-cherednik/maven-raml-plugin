package spring.general.dto;

/**
 * @author Oleg Cherednik
 * @since 05.07.2016
 */
public class User {
    /** {@example john.doe} */
    private String id;
    /** {@example John} */
    private String firstName;
    /** {@example Doe} */
    private String lastName;
    /** {@example John Patrick Doe} */
    private String fullName;
    /** {@example john.doe@yahoo.com} */
    private String email;
    private UserType userType;
}

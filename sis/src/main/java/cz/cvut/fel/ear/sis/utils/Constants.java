package cz.cvut.fel.ear.sis.utils;


import cz.cvut.fel.ear.sis.utils.enums.Role;

public final class Constants {

    /**
     * Default user role.
     */
    public static final Role DEFAULT_ROLE = Role.STUDENT;

    /**
     * Username login form parameter.
     */
    public static final String USERNAME_PARAM = "username";

    private Constants() {
        throw new AssertionError();
    }
}

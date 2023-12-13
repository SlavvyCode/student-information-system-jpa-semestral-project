package cz.cvut.fel.ear.sis.utils;


import cz.cvut.fel.ear.sis.utils.enums.Role;

import java.time.LocalDate;

public final class Constants {

    /**
     * Default user role.
     */
    public static final Role DEFAULT_ROLE = Role.STUDENT;

    /**
     * Username login form parameter.
     */
    public static final String USERNAME_PARAM = "username";


    public static final LocalDate AGE_OVER_18 = LocalDate.of(2000, 2, 2);

    private Constants() {
        throw new AssertionError();
    }
}

package cz.cvut.fel.ear.sis.utils;

import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.repository.SemesterRepository;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.exception.SemesterException;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

public class ServiceUtil {
    public static boolean doesNotConformRegex(String input, String regexPattern) {
        return input == null || !input.matches(regexPattern);
    }

}


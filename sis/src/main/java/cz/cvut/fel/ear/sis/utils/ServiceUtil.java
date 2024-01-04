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



//    public static LocalDate getNextSemesterStartDate(){





//        SemesterType nextSemesterType;


//        int nextSemesterYear = LocalDate.now().getYear();
//
//        //if now is during spring semester, current semester is spring
//        if(LocalDate.now().isAfter( LocalDate.of(LocalDate.now().getYear(), SemesterType.SPRING.getStartDate().getMonth(), SemesterType.SPRING.getStartDate().getDayOfMonth() ))
//                && LocalDate.now().isBefore(LocalDate.of(LocalDate.now().getYear(), SemesterType.SPRING.getEndDate().getMonth(), SemesterType.SPRING.getEndDate().getDayOfMonth())))
//
//            nextSemesterType = SemesterType.FALL;
//        else
//            nextSemesterType = SemesterType.SPRING;
//
//
//
//        if(nextSemesterType == SemesterType.SPRING)
//            nextSemesterYear++;
//
//
//        LocalDate nextSemesterStartDate = LocalDate.of(nextSemesterYear, nextSemesterType.getStartDate().getMonth(), nextSemesterType.getStartDate().getDayOfMonth());
//
//        return nextSemesterStartDate;


//    }



    public static boolean isNowDuringSemester(Semester semester){

        return LocalDate.now().isAfter(semester.getStartDate()) && LocalDate.now().isBefore(semester.getEndDate());

    }

    public static LocalDate getSemesterStartDate(){

        SemesterType semesterType;

        int semesterYear = LocalDate.now().getYear();

        //if now is during spring semester, current semester is spring
        if(LocalDate.now().isAfter( LocalDate.of(LocalDate.now().getYear(), SemesterType.SPRING.getStartDate().getMonth(), SemesterType.SPRING.getStartDate().getDayOfMonth() ))
                && LocalDate.now().isBefore(LocalDate.of(LocalDate.now().getYear(), SemesterType.SPRING.getEndDate().getMonth(), SemesterType.SPRING.getEndDate().getDayOfMonth())))
            semesterType = SemesterType.SPRING;
        else
            semesterType = SemesterType.FALL;


        return  LocalDate.of(semesterYear, semesterType.getStartDate().getMonth(), semesterType.getStartDate().getDayOfMonth());

    }


}


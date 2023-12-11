package cz.cvut.fel.ear.sis.dao.environment;

import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.utils.enums.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class TestDataGenerator {

//todo due to interconnectedness of our system, should be done in such a way so that it is not possible to generate the same thing twice, eg.
 //possibly in a way that we generate an entire scenario randomly instead of making individual methods

    //USED ONLY FOR TESTING PURPOSES!!!!
    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static int randomInt(int max) {
        return RAND.nextInt(max);
    }

    public static int randomInt(int min, int max) {
        assert min >= 0;
        assert min < max;

        int result;
        do {
            result = randomInt(max);
        } while (result < min);
        return result;
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static Person generateStudent() {

        Person student = new Student("firstName" + randomInt(), "lastName" + randomInt(), "jn1@fel.cz",
                "" + randomInt(), LocalDate.of(2000,3,3), "Ab"+ randomInt(),
                "studentKeyPass");

        return student;
    }

    public static Person generateAdmin() {

        Person admin = new Student("firstName" + randomInt(), "lastName" + randomInt(), "jn1@fel.cz",
                "" + randomInt(), LocalDate.of(2000,3,3), "Ab"+ randomInt(),
                "adminKeyPass");

        return admin;
    }


    public static Person generateTeacher() {

        Person teacher = new Teacher("firstName" + randomInt(), "lastName" + randomInt(), "jn1@fel.cz",
                "" + randomInt(), LocalDate.of(2000,3,3), "Ab"+ randomInt(),
                "adminKeyPass");

        return teacher;
    }

    public static Course generateCourse() {
        final Course course = new Course();
        course.setName("Course" + randomInt());
        course.setCode("C" + randomInt(1000, 9999));
        course.setECTS(randomInt(1, 10));
        // Add more fields as needed
        return course;
    }


    //todo add semester and course and classroom as parameters
    public static Parallel generateParallel() {
        //public Parallel(int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek, Semester semester, Classroom classroom, Course course){
        final Parallel parallel = new Parallel(randomInt(0, 50), TimeSlot.values()[randomInt(0, 6)], DayOfWeek.values()[randomInt(0, 4)], new Semester(), new Classroom(), new Course());
        return parallel;
    }
}

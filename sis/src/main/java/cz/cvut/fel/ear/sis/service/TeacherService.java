package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Course;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.model.Teacher;
import cz.cvut.fel.ear.sis.repository.CourseRepository;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.repository.StudentRepository;
import cz.cvut.fel.ear.sis.repository.TeacherRepository;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import cz.cvut.fel.ear.sis.utils.exception.CourseException;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static cz.cvut.fel.ear.sis.utils.ServiceUtil.doesNotConformRegex;

@Service
public class TeacherService {


    private final PersonRepository personRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public TeacherService(PersonRepository personRepository, StudentRepository studentRepository, TeacherRepository teacherRepository, CourseRepository courseRepository) {
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
    }
    //vytvoreni kurzu a paralelek


    public void createCourse(Teacher teacher, String courseName, String code, int ECTS,
                             Locale language) throws CourseException, PersonException {
        areCourseDetailsValid(teacher, courseName, code, ECTS, language);


        Course course = new Course(teacher, courseName, code, ECTS, language);


        courseRepository.save(course);
    }

    private boolean areCourseDetailsValid(Teacher teacher, String courseName, String code,
                                          int ECTS, Locale language) throws CourseException, PersonException {

        //check if teacher is not null,
        //the teacher should be valid - see  checks in personService
        if (teacher == null) {
            throw new PersonException("Teacher is not valid");
        }
        //check if course name is not empty and not too long
        if (courseName == null || doesNotConformRegex(courseName, "^[a-zA-ZáčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ0-9\\s.,!?()-]{3,50}$")) {
            throw new CourseException("Course name is not valid");
        }

        //check if course code is not empty and not too long
        if (code == null || code.length() > 10 || code.length()<3) {
            throw new CourseException("Course code is not valid");
        }
        //check if ECTS is not negative or too great
        if (ECTS < 0 || ECTS > 30) {
            throw new CourseException("ECTS is not valid");
        }
        //check if language is not empty and either "CZ" or "EN"
        if (language == null || language.getLanguage().equals("cz") || language.getLanguage().equals("en")) {
            throw new CourseException("Language is not valid");
        }

        return true;

    }

    public void createParallel(){

    }




    private boolean areParalellDetailsValid(int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek,
                                            Semester semester, Classroom classroom, Course course) throws CourseException, PersonException {

        //check if capacity is within the classroom's bounds


        //check if timeslot and day of week is valid


        //check if semester is valid


        //check if classroom valid


        //check if course valid


        return true;
    }
}

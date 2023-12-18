package cz.cvut.fel.ear.sis.rest;


import cz.cvut.fel.ear.sis.model.Course;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Student;
import cz.cvut.fel.ear.sis.rest.handler.utils.RestUtils;
import cz.cvut.fel.ear.sis.security.model.UserDetails;
import cz.cvut.fel.ear.sis.service.StudentService;
import cz.cvut.fel.ear.sis.service.TeacherService;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.Grade;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import cz.cvut.fel.ear.sis.utils.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;


import org.springframework.http.HttpStatus;

import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/student")
public class StudentController {

    private StudentService studentService;

    private static final Logger LOG = LoggerFactory.getLogger(TeacherController.class);


    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // include these functions
//The following 6 endpoints will cause:
//- NotStudentException in case the logged in user is not of type admin
//GET /student/course List all courses with parallels the next semester
//GET /student/parallel/{courseId} List all parallels for a given course available the next semester
//GET /student/schedule/{semesterCode} See all of parallels (and inherently the schedule, course, etc) that I am enrolled in for a given semester
//GET /student/report See report of all my enrollments including, semester, courseName, grade and status

//    kdyz student hleda kurzy, implementovat ordering a kriteria : kurz ma kapacitu a zaroven kurz jazyk == tzn dalsi
//    GET? Mozna? TODO CHECK THIS

//POST /student/enroll/{parallelId} Enroll in a next semester’s parallel


//DELETE /student/enroll/{parallelId} Revert such enrollment


}


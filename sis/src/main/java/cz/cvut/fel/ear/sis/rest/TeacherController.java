package cz.cvut.fel.ear.sis.rest;

import cz.cvut.fel.ear.sis.model.Course;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Student;
import cz.cvut.fel.ear.sis.rest.handler.utils.RestUtils;
import cz.cvut.fel.ear.sis.security.model.CustomUserDetails;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;


import org.springframework.http.HttpStatus;

import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/teacher")
public class TeacherController {

    private TeacherService teacherService;

    private static final Logger LOG = LoggerFactory.getLogger(TeacherController.class);




    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }




    // include these functions
//    GET /teacher/course List all of my courses
//    GET /teacher/parallel/{courseId} List all of parallels for my course id
//    GET /teacher/students/{parallelId} List all signed up students for my parallelId


//    POST /teacher/course Create a new course
//    POST /teacher/parallel/{courseId} Create a new parallel for a given course
//    POST /teacher/grade/{parallelId}/ {studentId}Grade a student within my parallel





    ///////////////////////////////////

    // GET MAPPING

    ///////////////////////////////////

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping(value = "/course", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Course>> listMyCourses(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Course> courses = teacherService.getCoursesByTeacherUsername(user.getUsername());
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping(value = "/parallel/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> listParallelsForCourse(@PathVariable Long courseId) {
        List<Parallel> parallels = teacherService.getParallelByCourseId(courseId);
        return new ResponseEntity<>(parallels, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping(value = "/students/{parallelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Student>> listStudentsForParallel(@PathVariable Long parallelId) {
        List<Student> students = teacherService.getAllStudentsByParallelId(parallelId);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }






    ///////////////////////////////////

    // POST MAPPING

    ///////////////////////////////////

//    POST /teacher/grade/{parallelId}/ {studentId}Grade a student within my parallel
//    POST /teacher/course Create a new course
//    POST /teacher/parallel/{courseId} Create a new parallel for a given course

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping(value = "/grade/{parallelId}/{studentId}", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void gradeStudent(@PathVariable Long parallelId, @PathVariable Long studentId, @RequestBody String gradeString)
            throws SemesterException, StudentException, EnrollmentException, CourseException {


        //check that grade string is valid
        Grade grade;
        try {
            grade = Grade.valueOf(gradeString);


        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid grade format", e);
        }

        teacherService.gradeStudent(parallelId, studentId, grade);
        LOG.debug("Graded student {} in parallel {}.", studentId, parallelId);
    }


















    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping(value = "/course", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> createCourse(@RequestParam String courseName,
                                               @RequestParam String code,
                                               @RequestParam int ECTS,
                                               @RequestParam String language)   throws CourseException, PersonException {

        //ensure that the language is a valid locale

        Locale locale;
        if (language != null) {
            try {
                locale = Locale.forLanguageTag(language);
            } catch (IllformedLocaleException e) {
                // Handle invalid locale format
                throw new IllegalArgumentException("Invalid locale format", e);
            }
        } else {
            // Use the system's default locale as a fallback
            locale = Locale.getDefault();
        }

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //get teacherId
        Long teacherId = teacherService.getTeacherByUsername(user.getUsername()).getId();


        teacherService.createCourse(teacherId, courseName, code, ECTS, locale);


        LOG.debug("Created course {}.", courseName);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", teacherId);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping(value = "/parallel/{courseId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Parallel> createParallel(@PathVariable Long courseId,
                                                   @RequestParam int capacity, @RequestParam String timeSlot,
                                                   @RequestParam String dayOfWeek, @RequestParam long semesterId,
                                                   @RequestParam long classroomId)
            throws SemesterException, ParallelException, ClassroomException, CourseException, PersonException {
        LOG.info("Received request with courseId: {}, capacity: {}, timeSlot: {}, dayOfWeek: {}, semesterId: {}, classroomId: {}",
                courseId, capacity, timeSlot, dayOfWeek, semesterId, classroomId);


        Course course = teacherService.getCourseById(courseId).orElseThrow(() -> new CourseException("Course not found"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long teacherId = teacherService.getTeacherByUsername(user.getUsername()).getId();


        Parallel parallel = teacherService.createParallel(teacherId, capacity, TimeSlot.valueOf(timeSlot),
                DayOfWeek.valueOf(dayOfWeek), semesterId, classroomId, courseId);


        LOG.debug("Created parallel {} for course {}.", parallel.getId(), course.getName());
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", parallel.getId());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }





}

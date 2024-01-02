package cz.cvut.fel.ear.sis.rest;


import cz.cvut.fel.ear.sis.model.Enrollment;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.repository.StudentRepository;
import cz.cvut.fel.ear.sis.security.model.CustomUserDetails;
import cz.cvut.fel.ear.sis.service.StudentService;
import cz.cvut.fel.ear.sis.utils.exception.*;
import cz.cvut.fel.ear.sis.utils.exception.rest.NotStudentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;


import org.springframework.http.HttpStatus;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/student")
public class StudentController {

    private StudentService studentService;

    private static final Logger LOG = LoggerFactory.getLogger(StudentController.class);


    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // include these functions


//The following 6 endpoints will cause:
//- NotStudentException in case the logged in user is not of type admin
//GET /student/course List all courses with parallels the next semester
//GET /student/schedule/{semesterCode} See all of parallels (and inherently the schedule, course, etc) that I am enrolled in for a given semester

//GET /student/parallel/{courseId} List all parallels for a given course available the next semester
//GET /student/report See report of all my enrollments including, semester, courseName, grade and status
//POST /student/enroll/{parallelId} Enroll in a next semester’s parallel
//DELETE /student/enroll/{parallelId} Revert such enrollment


//    kdyz student hleda kurzy, implementuj kriteria : kurz ma kapacitu a zaroven kurz jazyk == tzn dalsi GET asi




    ///////////////////////////////////
    // SPECIAL GET WITH CRITERIA API
    ///////////////////////////////////


    //GET List all parallels which have room for the student and have the desired language the next semester
    //GET /student/parallel/{courseId}/{language} List all parallels for a given course available the next semester

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @DeleteMapping(value = "/parallel/{courseId}/{language}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> listParallelsForCourseNextSemesterWithCourseIdLanguageCriteriaAPI(@PathVariable Long courseId, @PathVariable String language) {
        List<Parallel> parallels = studentService.getParallelsFromCourseNextSemesterWhereLanguageIsChosen(courseId, language);
        return new ResponseEntity<>(parallels, HttpStatus.OK);
    }


    ///////////////////////////////////
    // GET
    ///////////////////////////////////




//GET /student/course List all courses with parallels the next semester





    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/course", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> listCoursesForNextSemester(Authentication auth) throws ParallelException, NotStudentException {
        User user = (User) auth.getPrincipal();

        List<Parallel> parallels = studentService.getAllParallelsForNextSemester();
        return new ResponseEntity<>(parallels, HttpStatus.OK);
    }


    //GET /student/schedule/{semesterCode} See all parallels (and inherently the schedule, course, etc) that I am enrolled in for a given semester

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/schedule/{semesterCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> viewScheduleForSemester(@PathVariable String semesterCode, Authentication auth) throws ParallelException {
        User user = (User) auth.getPrincipal();
        List<Parallel> schedule = studentService.getAllEnrolledParallelsForNextSemesterByStudentUsername(user.getUsername(), semesterCode);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }



    //GET /student/parallel/{courseId} List all parallels for a given course available the next semester

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/parallel/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> listParallelsForCourseNextSemester(@PathVariable Long courseId) {
        List<Parallel> parallels = studentService.getParallelsForCourseNextSemester(courseId);
        return new ResponseEntity<>(parallels, HttpStatus.OK);
    }


//GET /student/report See report of all my enrollments including: semester, courseName, grade and status


    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Enrollment>> viewEnrollmentReport(Authentication auth) throws StudentException {
        User user = (User) auth.getPrincipal();
        List<Enrollment> report = studentService.getEnrollmentReportByUsername(user.getUsername());
        return new ResponseEntity<>(report, HttpStatus.OK);
    }



    ///////////////////////////////////
    // POST
    ///////////////////////////////////

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping(value = "/enroll/{parallelId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enrollInParallelNextSemester(@PathVariable Long parallelId, Authentication auth)
            throws EnrollmentException, SemesterException, CourseException, ParallelException, StudentException {
        User user = (User) auth.getPrincipal();
        studentService.enrollToParallelByUsername(user.getUsername(), parallelId);
        LOG.debug("Enrolled student {} in parallel {} for the next semester.", user.getUsername(), parallelId);
    }

    ///////////////////////////////////
    // DELETE
    ///////////////////////////////////

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @DeleteMapping(value = "/enroll/{parallelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revertEnrollment(@PathVariable Long parallelId, Authentication auth)
            throws EnrollmentException, SemesterException, ParallelException, StudentException, NotStudentException {
        User user = (User) auth.getPrincipal();
        studentService.dropFromParallelByUsername(user.getUsername(), parallelId);
        LOG.debug("Cancelled enrollment for student {} in parallel {} for next semester.", user.getUsername(), parallelId);
    }






}


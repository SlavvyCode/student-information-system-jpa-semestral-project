package cz.cvut.fel.ear.sis.rest;


import cz.cvut.fel.ear.sis.model.Enrollment;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.security.model.UserDetails;
import cz.cvut.fel.ear.sis.service.StudentService;
import cz.cvut.fel.ear.sis.utils.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    // SPECIAL GET WITH CRITERIA
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
    public ResponseEntity<List<Parallel>> listCoursesForNextSemester(Authentication auth) throws ParallelException {

        List<Parallel> parallels = studentService.getAllParallelsForNextSemester();

        return new ResponseEntity<>(parallels, HttpStatus.OK);
    }


    //GET /student/schedule/{semesterCode} See all parallels (and inherently the schedule, course, etc) that I am enrolled in for a given semester

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/schedule/{semesterCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> viewScheduleForSemester(@PathVariable String semesterCode, Authentication auth) throws ParallelException {
        Long studentId = ((UserDetails) auth.getPrincipal()).getId();
        List<Parallel> schedule = studentService.getAllEnrolledParallelsForNextSemester(studentId, semesterCode);
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

    //todo what does this mean, im pretty sure i dont still print out stuff

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Enrollment>> viewEnrollmentReport(Authentication auth) throws StudentException {
        Long studentId = ((UserDetails) auth.getPrincipal()).getId();
        List<Enrollment> report = studentService.getEnrollmentReport(studentId);
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
        Long studentId = ((UserDetails) auth.getPrincipal()).getId();
        studentService.enrollToParallel(studentId, parallelId);
        LOG.debug("Enrolled student {} in parallel {} for the next semester.", studentId, parallelId);
    }

    ///////////////////////////////////
    // DELETE
    ///////////////////////////////////

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @DeleteMapping(value = "/enroll/{parallelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revertEnrollment(@PathVariable Long parallelId, Authentication auth)
            throws EnrollmentException, SemesterException, ParallelException, StudentException {
        Long studentId = ((UserDetails) auth.getPrincipal()).getId();
        studentService.dropFromParallel(studentId, parallelId);
        LOG.debug("Cancelled enrollment for student {} in parallel {} for next semester.", studentId, parallelId);
    }






}


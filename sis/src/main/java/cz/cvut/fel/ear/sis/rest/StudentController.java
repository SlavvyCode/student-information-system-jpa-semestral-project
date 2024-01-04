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

    /**
     * Endpoint to list parallels for a given course next semester based on language criteria.
     *
     * @param courseId The unique identifier for the course.
     * @param language The language criteria to filter parallels.
     * @return List of parallels matching the criteria.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/parallel/{courseId}/{language}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> listParallelsForCourseNextSemesterWithCourseIdLanguageCriteriaAPI(@PathVariable Long courseId, @PathVariable String language) throws SemesterException {
        List<Parallel> parallels = studentService.getParallelsFromCourseNextSemesterWhereLanguageIsChosen(courseId, language);
        return new ResponseEntity<>(parallels, HttpStatus.OK);
    }

    /**
     * Endpoint to list all courses available for the next semester.
     *
     * @return List of all available parallels for the next semester.
     * @throws ParallelException If there's an issue fetching parallels.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/course", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> listCoursesForNextSemester() throws ParallelException, SemesterException {
        List<Parallel> parallels = studentService.getAllParallelsForNextSemester();
        return new ResponseEntity<>(parallels, HttpStatus.OK);
    }

    /**
     * Endpoint to view the schedule for a given semester.
     *
     * @param semesterCode The code identifying the semester.
     * @param auth The authentication details of the student.
     * @return List of enrolled parallels for the semester.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/schedule/{semesterCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> viewScheduleForSemester(@PathVariable String semesterCode, Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Parallel> schedule = studentService.getAllEnrolledParallelsForNextSemesterByStudentUsername(user.getUsername(), semesterCode);
        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    /**
     * Endpoint to list parallels for a given course in the next semester.
     *
     * @param courseId The unique identifier for the course.
     * @return List of parallels for the course in the next semester.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/parallel/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> listParallelsForCourseNextSemester(@PathVariable Long courseId) throws SemesterException {
        List<Parallel> parallels = studentService.getParallelsForCourseNextSemester(courseId);
        return new ResponseEntity<>(parallels, HttpStatus.OK);
    }

    /**
     * Endpoint to view the enrollment report for a student.
     *
     * @param auth The authentication details of the student.
     * @return List of enrollments for the student.
     * @throws StudentException If there's an issue fetching the enrollment report.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "/report", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Enrollment>> viewEnrollmentReport(Authentication auth) throws StudentException {
        User user = (User) auth.getPrincipal();
        List<Enrollment> report = studentService.getEnrollmentReportByUsername(user.getUsername());
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    /**
     * Endpoint to enroll a student in a parallel for the next semester.
     *
     * @param parallelId The unique identifier for the parallel.
     * @param auth The authentication details of the student.
     * @throws EnrollmentException If there's an issue enrolling the student.
     * @throws ParallelException If there's an issue with the parallel.
     * @throws StudentException If there's an issue with the student.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @PostMapping(value = "/enroll/{parallelId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enrollInParallelNextSemester(@PathVariable Long parallelId, Authentication auth) throws EnrollmentException, ParallelException, StudentException {
        User user = (User) auth.getPrincipal();
        studentService.enrollToParallelByUsername(user.getUsername(), parallelId);
        LOG.debug("Enrolled student {} in parallel {} for the next semester.", user.getUsername(), parallelId);
    }

    /**
     * Endpoint to cancel the enrollment of a student in a parallel for the next semester.
     *
     * @param parallelId The unique identifier for the parallel.
     * @param auth The authentication details of the student.
     * @throws ParallelException If there's an issue with the parallel.
     * @throws StudentException If there's an issue with the student.
     */
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @DeleteMapping(value = "/enroll/{parallelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revertEnrollment(@PathVariable Long parallelId, Authentication auth) throws ParallelException, StudentException, SemesterException, EnrollmentException {
        User user = (User) auth.getPrincipal();
        studentService.dropFromParallelByUsername(user.getUsername(), parallelId);
        LOG.debug("Cancelled enrollment for student {} in parallel {} for next semester.", user.getUsername(), parallelId);
    }

}


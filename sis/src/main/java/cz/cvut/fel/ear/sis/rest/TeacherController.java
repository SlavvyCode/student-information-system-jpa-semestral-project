package cz.cvut.fel.ear.sis.rest;

import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.repository.StudentRepository;
import cz.cvut.fel.ear.sis.rest.handler.utils.RestUtils;
import cz.cvut.fel.ear.sis.security.model.CustomUserDetails;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;


import org.springframework.http.HttpStatus;

import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/teacher")
public class TeacherController {

    private TeacherService teacherService;

    @Autowired
    private PersonRepository personRepository;


    private static final Logger LOG = LoggerFactory.getLogger(TeacherController.class);

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    /**
     * Endpoint to list all courses associated with the authenticated teacher.
     *
     * @param auth The authentication details of the teacher.
     * @return List of courses associated with the teacher.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping(value = "/course", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Course>> listMyCourses(Authentication auth) {
        User user = (User) auth.getPrincipal();
        List<Course> courses = teacherService.getCoursesByTeacherUsername(user.getUsername());
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    /**
     * Endpoint to list all parallels associated with a given course.
     *
     * @param courseId The unique identifier for the course.
     * @return List of parallels associated with the course.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping(value = "/parallel/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Parallel>> listParallelsForCourse(@PathVariable Long courseId) {
        List<Parallel> parallels = teacherService.getParallelByCourseId(courseId);
        return new ResponseEntity<>(parallels, HttpStatus.OK);
    }

    /**
     * Endpoint to list all students enrolled in a specific parallel.
     *
     * @param parallelId The unique identifier for the parallel.
     * @return List of students enrolled in the parallel.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping(value = "/students/{parallelId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Student>> listStudentsForParallel(@PathVariable Long parallelId) {
        List<Student> students = teacherService.getAllStudentsByParallelId(parallelId);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    /**
     * Endpoint to grade a student for a particular parallel.
     *
     * @param parallelId The unique identifier for the parallel.
     * @param studentId The unique identifier for the student.
     * @param gradeString The grade to assign to the student.
     * @throws SemesterException If there's an issue related to the semester.
     * @throws StudentException If there's an issue related to the student.
     * @throws EnrollmentException If there's an issue with enrollment.
     * @throws CourseException If there's an issue with the course.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping(value = "/grade/{parallelId}/{studentId}", consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void gradeStudent(@PathVariable Long parallelId,
                             @PathVariable Long studentId,
                             @RequestBody String gradeString) throws SemesterException, StudentException, EnrollmentException, CourseException {
        Grade grade;
        try {
            grade = Grade.valueOf(gradeString);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid grade format", e);
        }
        Enrollment enrollment =  teacherService.getEnrollmentByParallelIdAndStudentId(parallelId, studentId);
        Optional<Person> student = personRepository.findById(studentId);
        if(enrollment == null){
            throw new EnrollmentException("Enrollment not found");
        }
        teacherService.gradeStudent(studentId,enrollment.getId(), grade);
        LOG.debug("Graded student {} in parallel {}.", studentId, parallelId);
    }

    /**
     * Endpoint to create a new course.
     *
     * @param courseName The name of the course.
     * @param code The code of the course.
     * @param ECTS The ECTS credits for the course.
     * @param language The language tag for the course.
     * @return Response entity with headers indicating the location of the newly created course.
     * @throws CourseException If there's an issue with the course.
     * @throws PersonException If there's an issue related to the person.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping(value = "/course", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> createCourse(@RequestParam String courseName,
                                               @RequestParam String code,
                                               @RequestParam int ECTS,
                                               @RequestParam String language) throws CourseException, PersonException {
        Locale locale;
        if (language != null) {
            try {
                locale = Locale.forLanguageTag(language);
            } catch (IllformedLocaleException e) {
                throw new IllegalArgumentException("Invalid locale format", e);
            }
        } else {
            locale = Locale.getDefault();
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long teacherId = teacherService.getTeacherByUsername(user.getUsername()).getId();
        teacherService.createCourse(teacherId, courseName, code, ECTS, locale);
        LOG.debug("Created course {}.", courseName);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", teacherId);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Endpoint to create a new parallel for a given course.
     *
     * @param courseId The unique identifier for the course.
     * @param capacity The capacity of the parallel.
     * @param timeSlot The time slot for the parallel.
     * @param dayOfWeek The day of the week for the parallel.
     * @param semesterId The unique identifier for the semester.
     * @param classroomId The unique identifier for the classroom.
     * @return Response entity with headers indicating the location of the newly created parallel.
     * @throws SemesterException If there's an issue with the semester.
     * @throws ParallelException If there's an issue with the parallel.
     * @throws ClassroomException If there's an issue with the classroom.
     * @throws CourseException If there's an issue with the course.
     * @throws PersonException If there's an issue with the person.
     */
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping(value = "/parallel/{courseId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Parallel> createParallel(@PathVariable Long courseId,
                                                   @RequestParam int capacity,
                                                   @RequestParam String timeSlot,
                                                   @RequestParam String dayOfWeek,
                                                   @RequestParam long semesterId,
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

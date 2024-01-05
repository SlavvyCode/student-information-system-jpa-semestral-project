package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.utils.ServiceUtil;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.enums.Status;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import cz.cvut.fel.ear.sis.utils.exception.EnrollmentException;
import cz.cvut.fel.ear.sis.utils.exception.ParallelException;
import cz.cvut.fel.ear.sis.utils.exception.SemesterException;
import cz.cvut.fel.ear.sis.utils.exception.StudentException;
import cz.cvut.fel.ear.sis.utils.exception.rest.NotStudentException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.springframework.data.jpa.repository.query.QueryUtils.getQueryString;

@Service
public class StudentService {
    private final PersonRepository personRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParallelRepository parallelRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AdminService adminService;
    @PersistenceContext
    EntityManager em;

    private final SemesterRepository semesterRepository;

    @Autowired
    public StudentService(PersonRepository personRepository,
                          StudentRepository studentRepository,
                          TeacherRepository teacherRepository, ParallelRepository parallelRepository, EnrollmentRepository enrollmentRepository, AdminService adminService, SemesterRepository semesterRepository) {
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.parallelRepository = parallelRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.adminService = adminService;
        this.semesterRepository = semesterRepository;
    }

    /**
     * Enrolls a student in a parallel.
     *
     * @param studentId  The ID of the student to enroll.
     * @param parallelId The ID of the parallel to enroll in.
     * @return The created Enrollment object.
     * @throws StudentException   If the student is not found.
     * @throws ParallelException  If the parallel is not found.
     * @throws EnrollmentException If enrollment details are not valid.
     */
    public Enrollment enrollToParallel(long studentId, long parallelId) throws StudentException, ParallelException, EnrollmentException {
        Student student = studentRepository.findById(studentId).orElseThrow(()-> new StudentException("Teacher not found"));
        Parallel parallel = parallelRepository.findById(parallelId).orElseThrow(()-> new ParallelException("Parallel not found"));

        checkThatEnrollmentDetailsAreValid(parallel,student);

        Enrollment enrollment = new Enrollment(parallel, student);

        enrollmentRepository.save(enrollment);

        student.addEnrollment(enrollment);
        parallel.addStudent(student);

        studentRepository.save(student);
        parallelRepository.save(parallel);
        enrollmentRepository.save(enrollment);

        return enrollment;
    }




    /**
     * Retrieves a list of enrollments for a given student.
     *
     * @param studentId The ID of the student.
     * @return List of Enrollment objects.
     * @throws StudentException If the student is not found.
     */
    public List<Enrollment> getMyEnrollments(long studentId) throws StudentException {
        return studentRepository.findById(studentId).orElseThrow(()-> new StudentException("Student not found")).getMyEnrollments();
    }

    /**
     * Checks if the enrollment details are valid for a given student and parallel.
     *
     * @param parallel The parallel to check enrollment details for.
     * @param student  The student to check enrollment details for.
     * @return True if enrollment details are valid, otherwise false.
     * @throws ParallelException  If the parallel is not found.
     * @throws StudentException   If the student is not found.
     * @throws EnrollmentException If enrollment details are not valid.
     */
    public boolean checkThatEnrollmentDetailsAreValid(Parallel parallel,Student student) throws ParallelException, StudentException, EnrollmentException {
        long studentId = student.getId();
        long parallelId = parallel.getId();
        long parallelCourseId = parallel.getCourse().getId();
        long semesterId = parallel.getSemester().getId();
        DayOfWeek dayOfWeek = parallel.getDayOfWeek();
        TimeSlot timeSlot = parallel.getTimeSlot();



        SemesterType parallelSemesterType = parallel.getSemester().getSemesterType();

        LocalDate semesterStartDate = LocalDate.of(LocalDate.now().getYear(), parallelSemesterType.getStartDate().getMonth(), parallelSemesterType.getStartDate().getDayOfMonth());

        LocalDate parallelStartDate = parallel.getSemester().getStartDate();

//        Students can enroll only in class sections listed for the next semester and only during the current semester

        if(parallelStartDate.isBefore(semesterStartDate) ||
            parallelStartDate.isAfter(semesterStartDate.plusYears(1)))
            throw new EnrollmentException("Students can enroll only for the next semester");


        //check that parallel isn't full
        if(parallel.getStudents().size() >= parallel.getCapacity())
            throw new ParallelException("Parallel is full");


        //throw if student has more than 50 ECTS credits in this semester
        if(enrollmentRepository.getTotalECTSCreditsForStudentThisSemester(student, parallel.getSemester()) >= 50)
            throw new EnrollmentException("Student has more than 50 ECTS credits in this semester");

        //if student is already enrolled in a parallel of this course, throw
        List<Enrollment> courseEnrollments = enrollmentRepository.findAllByStudent_IdAndParallel_Course_Id(studentId, parallelCourseId);

        for(Enrollment enrollment : courseEnrollments){
            if(enrollment.getStatus().equals(Status.PASSED) || enrollment.getStatus().equals(Status.IN_PROGRESS))
                throw new EnrollmentException("Student can't be enrolled in a course which he has already passed or is in progress");


            if(enrollment.getParallel().getSemester().equals(parallel.getSemester()))
                throw new EnrollmentException("Student can't be enrolled in the same course twice in one semester");
        }

        if(courseEnrollments.size() >= 2)
            throw new EnrollmentException("Student can't be enrolled in a course more than twice");



        Enrollment enrollmentAtThisTimeSlot =
                enrollmentRepository.findByStudent_IdAndParallel_Semester_IdAndParallel_DayOfWeekAndParallel_TimeSlot
                        (studentId, semesterId, dayOfWeek, timeSlot);

        if(enrollmentAtThisTimeSlot != null)
                throw new StudentException("Student is already enrolled in a parallel at this time");




        return true;
    }

    /**
     * Withdraws a student's enrollment from a parallel.
     *
     * @param enrollmentId The ID of the enrollment to withdraw.
     * @throws EnrollmentException If the enrollment is not found or can't be withdrawn.
     */
    public void withdrawEnrollment(long enrollmentId) throws EnrollmentException {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(()-> new EnrollmentException("Enrollment not found"));
        Student student = enrollment.getStudent();

        if(enrollment.getParallel().getSemester().getStartDate().isBefore(LocalDate.now()))
            throw new EnrollmentException("Students can cancel enrollment only BEFORE the semester begins");

        student.removeEnrollment(enrollment);
    }

    /**
     * Retrieves a list of parallels for the next semester.
     *
     * @return List of Parallel objects for the next semester.
     * @throws ParallelException If parallels are not found for the next semester.
     */
    public List<Parallel> getAllParallelsForNextSemester() throws ParallelException, SemesterException {
        LocalDate nextSemesterStartDate = findNextSemester().getStartDate();
        return parallelRepository.findAllBySemester_StartDate(nextSemesterStartDate);
    }

    /**
     * Retrieves a list of enrolled parallels for a student in the next semester.
     *
     * @param studentId    The ID of the student.
     * @param semesterCode The code of the semester.
     * @return List of Parallel objects enrolled by the student in the next semester.
     * @throws ParallelException If enrolled parallels are not found for the next semester.
     */
    public List<Parallel> getAllEnrolledParallelsForNextSemester(long studentId, String semesterCode) throws ParallelException {
        return parallelRepository.findAllByStudents_IdAndSemester_Code(studentId,semesterCode);


    }

    /**
     * Retrieves a list of parallels for a course in the next semester.
     *
     * @param courseId The ID of the course.
     * @return List of Parallel objects for the course in the next semester.
     */
    public List<Parallel> getParallelsForCourseNextSemester(Long courseId) throws SemesterException {
        LocalDate nextSemesterStartDate = findNextSemester().getStartDate();
        return parallelRepository.findAllByCourse_IdAndSemester_StartDate(courseId, nextSemesterStartDate);
    }


        /**
         * Retrieves an enrollment report for a student.
         *
         * @param studentId The ID of the student.
         * @return List of Enrollment objects for the student.
         * @throws StudentException If the student is not found.
         */
    public List<Enrollment> getEnrollmentReport(Long studentId) throws StudentException {
        Student student = studentRepository.findById(studentId).orElseThrow(()-> new StudentException("Student not found"));
        return student.getMyEnrollments();
    }

    //CRITERIA API
    /**
     * Retrieves a list of parallels for a course in the next semester where the language is chosen.
     *
     * @param courseId The ID of the course.
     * @param language The language chosen for the course.
     * @return List of Parallel objects for the course in the next semester with the chosen language.
     */
    public List<Parallel> getParallelsFromCourseNextSemesterWhereLanguageIsChosen(Long courseId, String language) throws SemesterException {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Parallel> criteriaQuery = criteriaBuilder.createQuery(Parallel.class);
        Root<Parallel> from = criteriaQuery.from(Parallel.class);


        criteriaQuery.select(from);

        LocalDate nextSemesterStartDate = findNextSemester().getStartDate();

        criteriaQuery.where(
                criteriaBuilder.equal(from.get("course").get("id"), courseId),
                criteriaBuilder.equal(from.get("semester").get("startDate"), nextSemesterStartDate),
                criteriaBuilder.equal(from.get("course").get("language"), Locale.forLanguageTag(language)  )
        );
        TypedQuery<Parallel> query = em.createQuery(criteriaQuery);
        return query.getResultList();

    }

    /**
     * Retrieves a list of enrolled parallels for a student by username in the next semester.
     *
     * @param username     The username of the student.
     * @param semesterCode The code of the semester.
     * @return List of Parallel objects enrolled by the student in the next semester.
     */
    public List<Parallel> getAllEnrolledParallelsForNextSemesterByStudentUsername(String username,String semesterCode){
        return parallelRepository.findAllByStudents_UsernameAndSemester_Code(username,semesterCode);
    }

    /**
     * Retrieves an enrollment report for a student by username.
     *
     * @param username The username of the student.
     * @return List of Enrollment objects for the student.
     * @throws StudentException If the student is not found.
     */
    public List<Enrollment> getEnrollmentReportByUsername(String username) throws StudentException {
        Student student = studentRepository.findByUserName(username).orElseThrow(()-> new StudentException("Student not found"));
        return student.getMyEnrollments();
    }

    /**
     * Drops a student from a parallel by username.
     *
     * @param username   The username of the student to drop.
     * @param parallelId The ID of the parallel to drop from.
     * @throws StudentException  If the student is not found.
     * @throws ParallelException If the parallel is not found.
     */
    public void dropFromParallelByUsername(String username, Long parallelId) throws StudentException, ParallelException, SemesterException, EnrollmentException {
        Student student = studentRepository.findByUserName(username).orElseThrow(()-> new StudentException("Student not found"));
        Parallel parallel = parallelRepository.findById(parallelId).orElseThrow(()-> new ParallelException("Parallel not found"));
        dropFromParallel(student.getId(), parallelId);

    }

    /**
     * Enrolls a student in a parallel by username.
     *
     * @param username   The username of the student to enroll.
     * @param parallelId The ID of the parallel to enroll in.
     * @throws StudentException   If the student is not found.
     * @throws ParallelException  If the parallel is not found.
     * @throws EnrollmentException If enrollment details are not valid.
     */
    public void enrollToParallelByUsername(String username, Long parallelId) throws StudentException, ParallelException, EnrollmentException {
        Student student = studentRepository.findByUserName(username).orElseThrow(()-> new StudentException("Student not found"));
        Parallel parallel = parallelRepository.findById(parallelId).orElseThrow(()-> new ParallelException("Parallel not found"));
        enrollToParallel(student.getId(), parallelId);
    }


    /**
     * Drops a student from a parallel.
     *
     * @param studentId  The ID of the student to drop.
     * @param parallelId The ID of the parallel to drop from.
     * @throws ParallelException If the parallel is not found.
     * @throws StudentException  If the student is not found or not enrolled in the parallel.
     */
    @Transactional
    public void dropFromParallel(long studentId, long parallelId) throws ParallelException, StudentException, SemesterException, EnrollmentException {

        //if parallel exists, remove student from it and it from enrollment
        Student student = studentRepository.findById(studentId).orElseThrow(()-> new StudentException("Student not found"));

        Parallel parallel = parallelRepository.findById(parallelId).orElseThrow(()-> new ParallelException("Parallel not found"));
        if(!parallel.getStudents().contains(student))
            throw new StudentException("Student is not enrolled in this parallel");

        Semester activeSemester = semesterRepository.findSemesterByIsActiveIsTrue().orElseThrow(()-> new SemesterException("Active semester not found"));

        if(!parallel.getSemester().getStartDate().isAfter(activeSemester.getEndDate()) && !parallel.getSemester().getStartDate().isBefore(activeSemester.getStartDate().plusYears(1)))
            throw new EnrollmentException("Can't revert enrollment for a parallel that isn't in the next semester");


        Enrollment enrollmentToDrop = enrollmentRepository.findByStudent_IdAndParallel_Id(studentId, parallelId);
        if (enrollmentToDrop == null)
            throw new StudentException("Student is not enrolled in this parallel");


        student.removeEnrollment(enrollmentToDrop);
        parallel.removeStudent(student);

        enrollmentRepository.delete(enrollmentToDrop);
        parallelRepository.save(parallel);
        studentRepository.save(student);

    }


    /**
     * Finds the next semester.
     * @return The next semester.
     * @throws SemesterException If the next semester is not found.
     */
    private Semester findNextSemester() throws SemesterException {
        Semester activeSemester = semesterRepository.findSemesterByIsActiveIsTrue().orElseThrow(()-> new SemesterException("Active semester not found"));
        Semester nextSemester;

        if(activeSemester.getSemesterType().equals(SemesterType.SPRING))
            nextSemester = semesterRepository.findSemesterByCode("FALL"+activeSemester.getStartDate().getYear()).orElseThrow(()-> new SemesterException("Next semester not found, tried to find FALL"+activeSemester.getStartDate().getYear()));
        else
            nextSemester = semesterRepository.findSemesterByCode("SPRING"+(activeSemester.getStartDate().getYear()+1)).orElseThrow(()-> new SemesterException("Next semester not found, tried to find SPRING"+activeSemester.getStartDate().getYear()+1));

        return nextSemester;
    }


}

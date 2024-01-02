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
import cz.cvut.fel.ear.sis.utils.exception.StudentException;
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

    @PersistenceContext
    EntityManager em;



    private final SemesterRepository semesterRepository;

    @Autowired
    public StudentService(PersonRepository personRepository,
                          StudentRepository studentRepository,
                          TeacherRepository teacherRepository, ParallelRepository parallelRepository, EnrollmentRepository enrollmentRepository, SemesterRepository semesterRepository) {
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.parallelRepository = parallelRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.semesterRepository = semesterRepository;
    }

    //join a parallel group
    //student specific operations, like course enrollments and academic tracking

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


    @Transactional
    public void dropFromParallel(long studentId, long parallelId) throws ParallelException, StudentException {

        //if parallel exists, remove student from it and it from enrollment
        Student student = studentRepository.findById(studentId).orElseThrow(()-> new StudentException("Student not found"));

        Parallel parallel = parallelRepository.findById(parallelId).orElseThrow(()-> new ParallelException("Parallel not found"));
        if(!parallel.getStudents().contains(student))
            throw new StudentException("Student is not enrolled in this parallel");

        Enrollment enrollmentToDrop = enrollmentRepository.findByStudent_IdAndParallel_Id(studentId, parallelId);
        if (enrollmentToDrop == null)
            throw new StudentException("Student is not enrolled in this parallel");


        student.removeEnrollment(enrollmentToDrop);
        parallel.removeStudent(student);

        enrollmentRepository.delete(enrollmentToDrop);
        parallelRepository.save(parallel);
        studentRepository.save(student);

    }

    //this satifsies get Schedule and ECTS requirements
    public List<Enrollment> getMyEnrollments(long studentId) throws StudentException {
        return studentRepository.findById(studentId).orElseThrow(()-> new StudentException("Student not found")).getMyEnrollments();
    }


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


//        Students can withdraw their enrollment at any time before the semester begins.
    public void withdrawEnrollment(long enrollmentId) throws EnrollmentException {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(()-> new EnrollmentException("Enrollment not found"));
        Student student = enrollment.getStudent();

        if(enrollment.getParallel().getSemester().getStartDate().isBefore(LocalDate.now()))
            throw new EnrollmentException("Students can cancel enrollment only BEFORE the semester begins");

        student.removeEnrollment(enrollment);
    }



    public List<Parallel> getAllParallelsForNextSemester() throws ParallelException {
        return parallelRepository.findAllBySemester_StartDate(ServiceUtil.getNextSemesterStartDate());
    }


    public List<Parallel> getAllEnrolledParallelsForNextSemester(long studentId, String semesterCode) throws ParallelException {
        return parallelRepository.findAllByStudents_IdAndSemester_Code(studentId,semesterCode);


    }

    public List<Parallel> getParallelsForCourseNextSemester(Long courseId) {
        return parallelRepository.findAllByCourse_IdAndSemester_StartDate(courseId, ServiceUtil.getNextSemesterStartDate());
    }


    public List<Enrollment> getEnrollmentReport(Long studentId) throws StudentException {
        Student student = studentRepository.findById(studentId).orElseThrow(()-> new StudentException("Student not found"));
        return student.getMyEnrollments();
    }



    //CRITERIA API
    public List<Parallel> getParallelsFromCourseNextSemesterWhereLanguageIsChosen(Long courseId, String language) {

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Parallel> criteriaQuery = criteriaBuilder.createQuery(Parallel.class);
        Root<Parallel> from = criteriaQuery.from(Parallel.class);



        criteriaQuery.select(from);

        LocalDate nextSemesterStartDate = ServiceUtil.getNextSemesterStartDate();

        criteriaQuery.where(
                criteriaBuilder.equal(from.get("course").get("id"), courseId),
                criteriaBuilder.equal(from.get("semester").get("startDate"), nextSemesterStartDate),
                criteriaBuilder.equal(from.get("course").get("language"), Locale.forLanguageTag(language)  )
        );


        TypedQuery<Parallel> query = em.createQuery(criteriaQuery);
        return query.getResultList();

    }




    public List<Parallel> getAllEnrolledParallelsForNextSemesterByStudentUsername(String username,String semesterCode){
        return parallelRepository.findAllByStudents_UsernameAndSemester_Code(username,semesterCode);
    }

    public List<Enrollment> getEnrollmentReportByUsername(String username) {
        Student student = studentRepository.findByUsername(username).orElseThrow(()-> new StudentException("Student not found"));
        return student.getMyEnrollments();
    }
}

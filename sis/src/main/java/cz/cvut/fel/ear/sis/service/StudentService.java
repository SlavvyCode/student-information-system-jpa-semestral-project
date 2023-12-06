package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.Enrollment;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Student;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.enums.Status;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import cz.cvut.fel.ear.sis.utils.exception.EnrollmentException;
import cz.cvut.fel.ear.sis.utils.exception.ParallelException;
import cz.cvut.fel.ear.sis.utils.exception.StudentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {
    private final PersonRepository personRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParallelRepository parallelRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public StudentService(PersonRepository personRepository,
                          StudentRepository studentRepository,
                          TeacherRepository teacherRepository, ParallelRepository parallelRepository, EnrollmentRepository enrollmentRepository) {
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.parallelRepository = parallelRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    //join a parallel group
    //student specific operations, like course enrollments and academic tracking

    public Enrollment createEnrollmentToParallel(long studentId, long parallelId) throws StudentException, ParallelException, EnrollmentException {

        Student student = studentRepository.findById(studentId).orElseThrow(()-> new StudentException("Teacher not found"));

        Parallel parallel = parallelRepository.findById(parallelId).orElseThrow(()-> new ParallelException("Parallel not found"));


        checkThatEnrollmentDetailsAreValid(parallel,student);
        //check if all details check out
        //check if student is not already enrolled in this parallel group
        //add student to parallel group and course


        Enrollment enrollment = new Enrollment(parallel, student);


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

        parallelRepository.save(parallel);
        studentRepository.save(student);
    }

    //this satifsies Schedule and ECTS requirements
    public List<Enrollment> getMyEnrollments(long studentId) throws StudentException {
        return studentRepository.findById(studentId).orElseThrow(()-> new StudentException("Student not found")).getMyEnrollments();
    }



    public String getScheduleOfStudentInASemester(long studentId, long semesterId) {

        List<Enrollment> semestralCourses = enrollmentRepository.findAllByStudent_IdAndParallel_Semester_IdOrderByParallel_DayOfWeekAscParallel_TimeSlotAsc(studentId, semesterId);
        //sequentially print out on which days and at what time the student has classes

        StringBuilder schedule = new StringBuilder();
        Parallel parallel;

        for (Enrollment enrollment : semestralCourses) {
            parallel = enrollment.getParallel();
            schedule.append(parallel.getCourse().getName()).append(" ").append(parallel.getDayOfWeek()).append(" ").append(parallel.getTimeSlot()).append("\n");
        }

        return schedule.toString();
    }


    public String getSemesterGrades(long studentId, long semesterId) {
        List<Enrollment> semestralCourses = enrollmentRepository.findAllByStudent_IdAndParallel_Semester_IdOrderByParallel_DayOfWeekAscParallel_TimeSlotAsc(studentId, semesterId);
        //sequentially print out on which days and at what time the student has classes

        StringBuilder grades = new StringBuilder();
        Parallel parallel;

        for (Enrollment enrollment : semestralCourses) {
            parallel = enrollment.getParallel();
            grades.append(parallel.getCourse().getName()).append(" ").append(enrollment.getGrade()).append("\n");
        }

        return grades.toString();
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
}

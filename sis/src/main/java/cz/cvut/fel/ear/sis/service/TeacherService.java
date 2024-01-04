package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.model.Enrollment;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.utils.enums.*;
import cz.cvut.fel.ear.sis.utils.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static cz.cvut.fel.ear.sis.utils.ServiceUtil.doesNotConformRegex;

@Service
public class TeacherService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    private final ParallelRepository parallelRepository;
    private final SemesterRepository semesterRepository;
    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public TeacherService(StudentRepository studentRepository, TeacherRepository teacherRepository, CourseRepository courseRepository, ParallelRepository parallelRepository, SemesterRepository semesterRepository, ClassroomRepository classroomRepository, EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.parallelRepository = parallelRepository;
        this.semesterRepository = semesterRepository;
        this.classroomRepository = classroomRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Creates a new course with the provided details.
     *
     * @param teacherId   The ID of the teacher creating the course.
     * @param courseName  The name of the course.
     * @param code        The code of the course.
     * @param ECTS        The ECTS credits for the course.
     * @param language    The language of the course (either "CZ" or "EN").
     * @return The created Course object.
     * @throws CourseException  If course details are not valid.
     * @throws PersonException  If the teacher is not found or not valid.
     */
    @Transactional
    public Course createCourse(long teacherId,
                               String courseName,
                               String code,
                               int ECTS,Locale language) throws CourseException, PersonException {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(()-> new PersonException("Teacher not found"));
        areCourseDetailsValid(teacher, courseName, code, ECTS, language);
        Course course = new Course(teacher, courseName, code, ECTS, language);
        courseRepository.save(course);
        teacherRepository.save(teacher);
        return course;
    }

    /**
     * Validates the course details based on the provided parameters.
     *
     * @param teacher     The teacher object.
     * @param courseName  The name of the course.
     * @param code        The code of the course.
     * @param ECTS        The ECTS credits for the course.
     * @param language    The language of the course.
     * @throws CourseException  If course details are not valid.
     * @throws PersonException  If the teacher is not valid.
     */
    private void areCourseDetailsValid(Teacher teacher,
                                       String courseName,
                                       String code,
                                       int ECTS,
                                       Locale language) throws CourseException, PersonException {
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
        if (language == null || (!language.equals(Locale.ENGLISH) && !language.equals(Locale.forLanguageTag("CZ")))   ) {
            throw new CourseException("Language is not valid");
        }

    }

    /**
     * Creates a new parallel with the provided details.
     *
     * @param teacherId   The ID of the teacher creating the parallel.
     * @param capacity    The capacity of the parallel.
     * @param timeSlot    The time slot for the parallel.
     * @param dayOfWeek   The day of the week for the parallel.
     * @param semesterId  The ID of the semester for the parallel.
     * @param classroomId The ID of the classroom for the parallel.
     * @param courseId    The ID of the course for the parallel.
     * @return The created Parallel object.
     * @throws CourseException    If course details are not valid.
     * @throws PersonException    If the teacher is not found or not valid.
     * @throws ParallelException  If parallel details are not valid.
     * @throws ClassroomException If the classroom is not found.
     * @throws SemesterException  If the semester is not found.
     */
    @Transactional
    public Parallel createParallel(long teacherId,
                                   int capacity,
                                   TimeSlot timeSlot,
                                   DayOfWeek dayOfWeek,
                                   long semesterId,
                                   long classroomId,
                                   long courseId) throws CourseException, PersonException, ParallelException, ClassroomException, SemesterException {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(()-> new PersonException("Teacher not found"));
        Semester semester = semesterRepository.findById(semesterId).orElseThrow(()-> new SemesterException("Semester not found"));
        Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(()-> new ClassroomException("Classroom not found"));
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new CourseException("Course not found"));



        areParalellDetailsValid(teacher, capacity, timeSlot, dayOfWeek, semester, classroom, course);

        Parallel parallel = new Parallel(capacity, timeSlot, dayOfWeek, semester, classroom, course);

        course.addParallel(parallel);
        parallelRepository.save(parallel);
        courseRepository.save(course);

        return parallel;
    }

    /**
     * Validates the parallel details based on the provided parameters.
     *
     * @param teacher   The teacher object.
     * @param capacity  The capacity of the parallel.
     * @param timeSlot  The time slot for the parallel.
     * @param dayOfWeek The day of the week for the parallel.
     * @param semester  The semester for the parallel.
     * @param classroom The classroom for the parallel.
     * @param course    The course for the parallel.
     * @throws PersonException    If the teacher is not valid.
     * @throws ParallelException  If parallel details are not valid.
     * @throws ClassroomException If the classroom already has an occupied time slot.
     * @throws SemesterException  If the semester date is not valid.
     */
    private boolean areParalellDetailsValid(Teacher teacher,
                                            int capacity,
                                            TimeSlot timeSlot,
                                            DayOfWeek dayOfWeek,
                                            Semester semester,
                                            Classroom classroom,
                                            Course course) throws PersonException, ParallelException, ClassroomException, SemesterException {

        //check if capacity is within the classroom's bounds
        if(capacity<=0 || capacity>classroom.getCapacity()){
            throw new ParallelException("Capacity is not valid");
        }

        //can only make a parallel 2 semesters in advance
        if (semester.getSemesterType() == SemesterType.SPRING){

            LocalDate thisYearsDefaultSpringSemesterStartDate = LocalDate.of(LocalDate.now().getYear(),
                    SemesterType.SPRING.getStartDate().getMonth(),SemesterType.SPRING.getStartDate().getDayOfMonth());

            if(semester.getStartDate().isAfter(thisYearsDefaultSpringSemesterStartDate.plusYears(2)) ||
                    semester.getStartDate().isBefore(thisYearsDefaultSpringSemesterStartDate))
                throw new SemesterException("Semester date is not valid");
        }
        else
        {

            LocalDate thisYearsDefaultFallSemesterStartDate = LocalDate.of(LocalDate.now().getYear(),
                    SemesterType.FALL.getStartDate().getMonth(),SemesterType.FALL.getStartDate().getDayOfMonth());

            if(semester.getStartDate().isAfter(thisYearsDefaultFallSemesterStartDate.plusYears(2)) ||
                    semester.getStartDate().isBefore(thisYearsDefaultFallSemesterStartDate))
                throw new SemesterException("Semester date is not valid");
        }

        //check if classroom already has a parallel with the timeslot and day of week occupied
        List<Parallel> sameTimeSlotParallels = parallelRepository.findByClassroomAndSemesterAndDayOfWeekAndTimeSlot(classroom, semester,dayOfWeek, timeSlot);
        if(!sameTimeSlotParallels.isEmpty())
            throw new ClassroomException("That classroom already has a parallel with this timeslot occupied");

        //throw if teacher teaches more than one course
        List<Course> teacherCourses =  courseRepository.findAllByTeacher_Id(teacher.getId());
        if(teacherCourses.size()>1)
            throw new PersonException("Teacher teaches multiple courses!");

        //throw if teacher teaches a different course
        if(teacherCourses.size()==1 && !teacherCourses.get(0).equals(course))
            throw new PersonException("Teacher already teaches another course");


        return true;
    }

    /**
     * Updates the details of an existing course.
     *
     * @param courseId   The ID of the course to update.
     * @param teacherId  The ID of the teacher updating the course.
     * @param courseName The new name of the course.
     * @param code       The new code of the course.
     * @param ECTS       The new ECTS credits for the course.
     * @param language   The new language of the course.
     * @throws CourseException If course details are not valid.
     * @throws PersonException If the teacher is not found or not valid.
     */
    @Transactional
    public void updateCourse(long courseId,
                             long teacherId,
                             String courseName,
                             String code,
                             int ECTS,
                             Locale language) throws CourseException, PersonException {
        Course course = courseRepository.findById(courseId).orElseThrow(()-> new CourseException("Course not found"));
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(()-> new PersonException("Teacher not found"));

        areCourseDetailsValid(teacher, courseName, code, ECTS, language);

        Course oldCourse = teacher.getMyCourses().get(0);
        course.setTeacher(teacher);
        course.setName(courseName);
        course.setCode(code);
        course.setECTS(ECTS);
        course.setLanguage(language);


        teacher.removeCourse(oldCourse);
        teacher.addCourse(course);

        courseRepository.save(course);
        teacherRepository.save(teacher);
    }

    /**
     * Grades a student based on the provided enrollment ID and grade.
     *
     * @param studentId    The ID of the student.
     * @param enrollmentId The ID of the enrollment.
     * @param grade        The grade to assign to the student.
     * @throws StudentException    If the student is not found or not valid.
     * @throws EnrollmentException If the enrollment is not found or already graded.
     */
    @Transactional
    public void gradeStudent(long studentId, long enrollmentId, Grade grade) throws StudentException, EnrollmentException {


        Student student = studentRepository.findById(studentId).orElseThrow(()-> new StudentException("Student not found"));

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(()-> new EnrollmentException("Enrollment not found"));

        if(!enrollment.getStudent().equals(student))
            throw new StudentException("Student is not enrolled in this parallel");

        if(enrollment.getStatus().equals(Status.PASSED) || enrollment.getStatus().equals(Status.FAILED))
            throw new EnrollmentException("Student has already been graded");

        if(enrollment.getParallel().getSemester().getStartDate().isAfter(LocalDate.now()))
            throw new EnrollmentException("Student can't be graded before the semester begins");

        if(enrollment.getParallel().getSemester().getEndDate().isBefore(LocalDate.now()))
            throw new EnrollmentException("Student can't be graded after the semester ends");



        enrollment.setGrade(grade);
        enrollmentRepository.save(enrollment);



    }

    /**
     * Updates the details of an existing parallel.
     *
     * @param parallel   The parallel object to update.
     * @param teacher    The teacher object.
     * @param capacity   The new capacity of the parallel.
     * @param timeSlot   The new time slot for the parallel.
     * @param dayOfWeek  The new day of the week for the parallel.
     * @param semester   The new semester for the parallel.
     * @param classroom  The new classroom for the parallel.
     * @param course     The new course for the parallel.
     * @throws PersonException    If the teacher is not valid.
     * @throws ParallelException  If parallel details are not valid.
     * @throws SemesterException  If the semester date is not valid.
     * @throws ClassroomException If the classroom already has an occupied time slot.
     */
    @Transactional
    public void updateParallel(Parallel parallel,
                               Teacher teacher,
                               int capacity,
                               TimeSlot timeSlot,
                               DayOfWeek dayOfWeek,
                               Semester semester,
                               Classroom classroom,
                               Course course) throws PersonException, ParallelException, SemesterException, ClassroomException {
        areParalellDetailsValid(teacher, capacity, timeSlot, dayOfWeek, semester, classroom, course);

        Course oldCourse = parallel.getCourse();
        oldCourse.removeParallel(parallel);

        parallel.setCapacity(capacity);
        parallel.setTimeSlot(timeSlot);
        parallel.setDayOfWeek(dayOfWeek);
        parallel.setSemester(semester);
        parallel.setClassroom(classroom);
        parallel.setCourse(course);


        course.addParallel(parallel);

        parallelRepository.save(parallel);
        courseRepository.save(oldCourse);
        courseRepository.save(course);
    }

    /**
     * Retrieves all courses.
     *
     * @return List of all courses.
     */
    @Transactional(readOnly = true)
    public List<Course> getAllCoruses(){
        return courseRepository.findAll();
    }

    /**
     * Retrieves all parallels.
     *
     * @return List of all parallels.
     */
    @Transactional(readOnly = true)
    public List<Parallel> getAllParallels(){
        return parallelRepository.findAll();
    }

    /**
     * Retrieves parallels for the upcoming semester associated with a teacher.
     *
     * @param teacherId The ID of the teacher.
     * @return List of parallels for the upcoming semester.
     * @throws SemesterException If the semester is not found.
     */
    @Transactional(readOnly = true)
    public List<Parallel> getNextSemesterTeacherParallels(long teacherId) throws SemesterException {
        //Teachers may list parallels only for the upcoming semester

        LocalDate springSemesterStartDate = LocalDate.of(LocalDate.now().getYear(),
                SemesterType.SPRING.getStartDate().getMonth(),SemesterType.SPRING.getStartDate().getDayOfMonth());
        LocalDate fallSemesterStartDate = LocalDate.of(LocalDate.now().getYear(),
                SemesterType.FALL.getStartDate().getMonth(),SemesterType.FALL.getStartDate().getDayOfMonth());


        LocalDate springSemesterEndDate = LocalDate.of(LocalDate.now().getYear(),
                SemesterType.SPRING.getEndDate().getMonth(),SemesterType.SPRING.getEndDate().getDayOfMonth());
        LocalDate fallSemesterEndDate = LocalDate.of(LocalDate.now().getYear(),
                SemesterType.FALL.getEndDate().getMonth(),SemesterType.FALL.getEndDate().getDayOfMonth());




        Semester nextSemester;
        if(LocalDate.now().isAfter(springSemesterStartDate) && LocalDate.now().isBefore(springSemesterEndDate))
        {
            nextSemester = semesterRepository.findByStartDate(fallSemesterStartDate);
        }
        else
        {
            LocalDate nextSpringSemesterDate = springSemesterStartDate.plusYears(1);
            nextSemester = semesterRepository.findByStartDate(nextSpringSemesterDate);

        }

        if(nextSemester==null)
            throw new SemesterException("Semester not found");

        return parallelRepository.findAllBySemester_StartDateAndCourse_Teacher_Id(nextSemester.getStartDate(), teacherId);

    }

    /**
     * Retrieves all teachers.
     *
     * @return List of all teachers.
     */
    @Transactional(readOnly = true)
    public List<Teacher> getAllTeachers(){
        return teacherRepository.findAll();
    }

    /**
     * Retrieves all students associated with a course.
     *
     * @param course The course for which students are to be retrieved.
     * @return List of students associated with the course.
     */
    @Transactional(readOnly = true)
    public List<Student> getAllStudentsFromCourse(Course course){
        return studentRepository.findAllByCourse(course);
    }

    /**
     * Retrieves all students associated with a parallel.
     *
     * @param parallel The parallel for which students are to be retrieved.
     * @return List of students associated with the parallel.
     */
    @Transactional(readOnly = true)
    public List<Student> getAllStudentsFromParallel(Parallel parallel){
        Course course = parallel.getCourse();
        return studentRepository.findAllByParallel(parallel.getId(), course);
    }

    /**
     * Retrieves a teacher by ID.
     *
     * @param id The ID of the teacher.
     * @return Optional containing the teacher, or empty if not found.
     */
    @Transactional(readOnly = true)
    public Optional<Teacher> getTeacherById(Long id){
        return teacherRepository.findById(id);
    }

    /**
     * Retrieves a course by ID.
     *
     * @param id The ID of the course.
     * @return Optional containing the course, or empty if not found.
     */

    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Long id){
        return courseRepository.findById(id);
    }

    /**
     * Retrieves courses by a teacher's ID.
     *
     * @param id The ID of the teacher.
     * @return List of courses associated with the teacher.
     */
    @Transactional(readOnly = true)
    public List<Course> getCourseByTeacherId(Long id){
        return courseRepository.findAllByTeacher_Id(id);
    }

    /**
     * Retrieves a parallel by ID.
     *
     * @param id The ID of the parallel.
     * @return Optional containing the parallel, or empty if not found.
     */
    @Transactional(readOnly = true)
    public Optional<Parallel> getParallelById(Long id){
        return parallelRepository.findById(id);
    }

    /**
     * Retrieves parallels by a course's ID.
     *
     * @param id The ID of the course.
     * @return List of parallels associated with the course.
     */
    @Transactional(readOnly = true)
    public List<Parallel> getParallelByCourseId(Long id){
        return parallelRepository.findAllByCourse_Id(id);
    }

    /**
     * Retrieves all students by a parallel's ID.
     *
     * @param id The ID of the parallel.
     * @return List of students associated with the parallel.
     */
    @Transactional(readOnly = true)
    public List<Student> getAllStudentsByParallelId(Long id){
        return studentRepository.findAllByParallelId(id);
    }

    /**
     * Retrieves courses by a teacher's username.
     *
     * @param username The username of the teacher.
     * @return List of courses associated with the teacher's username.
     */
    @Transactional(readOnly = true)
    public List<Course> getCoursesByTeacherUsername(String username){
        return getCoursesByTeacherUsername(username);
    }

    /**
     * Retrieves a teacher by username.
     *
     * @param username The username of the teacher.
     * @return The teacher associated with the given username.
     */
    public Teacher getTeacherByUsername(String username) {
        return teacherRepository.findByUserName(username);
    }
}

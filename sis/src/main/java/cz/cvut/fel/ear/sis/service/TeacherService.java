package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.*;
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


    private final PersonRepository personRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;

    private final ParallelRepository parallelRepository;
    private final SemesterRepository semesterRepository;
    private final ClassroomRepository classroomRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public TeacherService(PersonRepository personRepository, StudentRepository studentRepository, TeacherRepository teacherRepository, CourseRepository courseRepository, ParallelRepository parallelRepository, SemesterRepository semesterRepository, ClassroomRepository classroomRepository, EnrollmentRepository enrollmentRepository) {
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.parallelRepository = parallelRepository;
        this.semesterRepository = semesterRepository;
        this.classroomRepository = classroomRepository;
        this.enrollmentRepository = enrollmentRepository;
    }
    //vytvoreni kurzu a paralelek

    @Transactional
    public Course createCourse(long teacherId, String courseName, String code, int ECTS,
                               Locale language) throws CourseException, PersonException {

        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(()-> new PersonException("Teacher not found"));
        areCourseDetailsValid(teacher, courseName, code, ECTS, language);
        Course course = new Course(teacher, courseName, code, ECTS, language);
        courseRepository.save(course);
        teacherRepository.save(teacher);
        return course;
    }


    private boolean areCourseDetailsValid(Teacher teacher, String courseName, String code,
                                          int ECTS, Locale language) throws CourseException, PersonException {

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

        return true;

    }

    @Transactional
    public Parallel createParallel(long teacherId, int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek,
                                   long semesterId, long classroomId, long courseId) throws CourseException, PersonException, ParallelException, ClassroomException, SemesterException {

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



    private boolean areParalellDetailsValid(Teacher teacher, int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek,
                                            Semester semester, Classroom classroom, Course course) throws CourseException, PersonException, ParallelException, ClassroomException, SemesterException {

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



    @Transactional
    public void updateCourse(long courseId, long teacherId, String courseName, String code, int ECTS,
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

    @Transactional
    public void gradeStudent(long studentId, long enrollmentId, Grade grade) throws StudentException, CourseException, SemesterException, EnrollmentException {


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

    @Transactional
    public void updateParallel(Parallel parallel, Teacher teacher, int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek,
                               Semester semester, Classroom classroom, Course course) throws CourseException, PersonException, ParallelException, SemesterException, ClassroomException {
        areParalellDetailsValid(teacher, capacity, timeSlot, dayOfWeek, semester, classroom, course);

        Course oldCourse = parallel.getCourse();
        //old course remove
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


    // todo deleting
    //  asi by bylo lechci udelat nejak kaskadove? vlastne jsme resili nejak jestli to ma smysl





    @Transactional(readOnly = true)
    public List<Course> getAllCoruses(){
        return courseRepository.findAll();
    }
    @Transactional(readOnly = true)
    public List<Parallel> getAllParallels(){
        return parallelRepository.findAll();
    }
    @Transactional(readOnly = true)
    public List<Teacher> getAllTeachers(){
        return teacherRepository.findAll();
    }
    @Transactional(readOnly = true)
    public List<Student> getAllStudentsFromCourse(Course course){
        return studentRepository.findAllByCourse(course);
    }
    @Transactional(readOnly = true)
    public List<Student> getAllStudentsFromParallel(Parallel parallel){
        Course course = parallel.getCourse();
        return studentRepository.findAllByParallel(parallel.getId(), course);
    }

    @Transactional(readOnly = true)
    public Optional<Teacher> getTeacherById(Long id){
        return teacherRepository.findById(id);
    }
    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Long id){
        return courseRepository.findById(id);
    }
    @Transactional(readOnly = true)
    public List<Course> getCourseByTeacherId(Long id){
        return courseRepository.findAllByTeacher_Id(id);
    }
    @Transactional(readOnly = true)
    public Optional<Parallel> getParallelById(Long id){
        return parallelRepository.findById(id);
    }
}

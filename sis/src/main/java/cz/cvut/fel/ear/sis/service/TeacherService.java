package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
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

    @Autowired
    public TeacherService(PersonRepository personRepository, StudentRepository studentRepository, TeacherRepository teacherRepository, CourseRepository courseRepository, ParallelRepository parallelRepository, SemesterRepository semesterRepository, ClassroomRepository classroomRepository) {
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.parallelRepository = parallelRepository;
        this.semesterRepository = semesterRepository;
        this.classroomRepository = classroomRepository;
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


        //todo maty zkontroluj? zda se mi to logicke
        //can only make a parallel 2 semesters in advance
        if(semester.getStartDate().isAfter(LocalDate.now().plusYears(2)) ||
           semester.getStartDate().isBefore(LocalDate.now()))
            throw new SemesterException("Semester date is not valid");

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



        course.setTeacher(teacher);
        course.setName(courseName);
        course.setCode(code);
        course.setECTS(ECTS);
        course.setLanguage(language);

        courseRepository.save(course);
    }
    @Transactional
    public void updateParallel(Parallel parallel, Teacher teacher, int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek,
                               Semester semester, Classroom classroom, Course course) throws CourseException, PersonException, ParallelException, SemesterException, ClassroomException {
        areParalellDetailsValid(teacher, capacity, timeSlot, dayOfWeek, semester, classroom, course);

        parallel.setCapacity(capacity);
        parallel.setTimeSlot(timeSlot);
        parallel.setDayOfWeek(dayOfWeek);
        parallel.setSemester(semester);
        parallel.setClassroom(classroom);
        parallel.setCourse(course);

        parallelRepository.save(parallel);
    }


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

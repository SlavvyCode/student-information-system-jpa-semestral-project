package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.model.Student;
import cz.cvut.fel.ear.sis.repository.ClassroomRepository;
import cz.cvut.fel.ear.sis.repository.SemesterRepository;
import cz.cvut.fel.ear.sis.repository.StudentRepository;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.exception.ClassroomException;
import cz.cvut.fel.ear.sis.utils.exception.SemesterException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final SemesterRepository semesterRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public AdminService(
            SemesterRepository semesterRepository,
            ClassroomRepository classroomRepository,
            StudentRepository studentRepository
    ){
        this.semesterRepository = semesterRepository;
        this.classroomRepository = classroomRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Creates a new semester with the specified year and type.
     *
     * @param year         The year of the semester.
     * @param semesterType The type of the semester (e.g., FALL, SPRING).
     * @return The created Semester object.
     * @throws SemesterException If a semester with the same code already exists.
     */
    @Transactional
    public Semester createSemester(int year, SemesterType semesterType) throws SemesterException {
        if (semesterExists(semesterType.name() + year))
            throw new SemesterException("Semester already exists");
        Semester semester = new Semester(year, semesterType);
        semesterRepository.save(semester);
        return semester;
    }

    /**
     * Retrieves a semester by its unique code.
     *
     * @param code The code of the semester.
     * @return Optional containing the Semester object if found, otherwise empty.
     */
    @Transactional
    public Optional<Semester> getSemesterByCode(String code){
        return semesterRepository.findSemesterByCode(code);
    }

    /**
     * Checks if a semester exists by its code.
     *
     * @param code The code of the semester.
     * @return True if the semester exists, otherwise false.
     */
    @Transactional
    public boolean semesterExists(String code){
        return getSemesterByCode(code).isPresent();
    }

    /**
     * Retrieves the active semester in the system.
     *
     * @return Optional containing the active Semester object if found, otherwise empty.
     */
    @Transactional
    public Optional<Semester> getActiveSemester(){
        return semesterRepository.findSemesterByIsActiveIsTrue();
    }

    /**
     * Sets the provided semester as the active semester.
     *
     * @param semester The semester to set as active.
     */
    @Transactional
    public void setActiveSemester(Semester semester){
        Optional<Semester> activeSemester = getActiveSemester();
        boolean activeSemesterExists = activeSemester.isPresent();
        if (activeSemesterExists){
            Semester active = getActiveSemester().get();
            active.setActive(false);
            semesterRepository.save(active);
        }
        semester.setActive(true);
        semesterRepository.save(semester);
    }

    /**
     * Retrieves a list of all semesters in the system.
     *
     * @return List of Semester objects.
     */
    @Transactional
    public List<Semester> getAllSemesters(){
        return semesterRepository.findAll();
    }

    /**
     * Creates a new classroom with the specified code and capacity.
     *
     * @param code     The code of the classroom.
     * @param capacity The capacity of the classroom.
     * @return The created Classroom object.
     * @throws ClassroomException If a classroom with the same code already exists or capacity is invalid.
     */
    @Transactional
    public Classroom createClassroom(String code, int capacity) throws ClassroomException {
        if (classroomExists(code)) throw new ClassroomException("Classroom with such code already exists");
        if (capacity < 1 || capacity > 200) throw new ClassroomException("Classroom capacity must be 1-200.");
        Classroom classroom = new Classroom(code, capacity);
        classroomRepository.save(classroom);
        return classroom;
    }

    /**
     * Retrieves a classroom by its unique code.
     *
     * @param code The code of the classroom.
     * @return Optional containing the Classroom object if found, otherwise empty.
     */
    @Transactional
    public Optional<Classroom> getClassroomByCode(String code){
        return classroomRepository.findClassroomByCode(code);
    }

    /**
     * Checks if a classroom exists by its code.
     *
     * @param code The code of the classroom.
     * @return True if the classroom exists, otherwise false.
     */
    @Transactional
    public boolean classroomExists(String code){
        return getClassroomByCode(code).isPresent();
    }

    /**
     * Retrieves a list of all classrooms in the system.
     *
     * @return List of Classroom objects.
     */
    @Transactional
    public List<Classroom> getAllClassrooms(){
        return classroomRepository.findAll();
    }

    /**
     * Deletes a student from the system by their ID.
     *
     * @param studentId The ID of the student to delete.
     * @return True if the student was deleted successfully, otherwise false.
     */
    @Transactional
    public boolean deleteStudent(long studentId){
        Optional<Student> studentOptional =  studentRepository.findById(studentId);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            studentRepository.delete(student);
            return true;
        } else {
            return false;
        }
    }
    public Semester findNextSemester() throws SemesterException {
        Semester activeSemester = semesterRepository.findSemesterByIsActiveIsTrue().orElseThrow(()-> new SemesterException("Active semester not found"));
        Semester nextSemester;

        if(activeSemester.getSemesterType().equals(SemesterType.SPRING))
            nextSemester = semesterRepository.findSemesterByCode("FALL"+activeSemester.getStartDate().getYear()).orElseThrow(()-> new SemesterException("Next semester not found, tried to find FALL"+activeSemester.getStartDate().getYear()));
        else
            nextSemester = semesterRepository.findSemesterByCode("SPRING"+(activeSemester.getStartDate().getYear()+1)).orElseThrow(()-> new SemesterException("Next semester not found, tried to find SPRING"+activeSemester.getStartDate().getYear()+1));

        return nextSemester;
    }
}

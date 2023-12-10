package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.repository.ClassroomRepository;
import cz.cvut.fel.ear.sis.repository.SemesterRepository;
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

    @Autowired
    public AdminService(
            SemesterRepository semesterRepository,
            ClassroomRepository classroomRepository
    ){
        this.semesterRepository = semesterRepository;
        this.classroomRepository = classroomRepository;
    }

    @Transactional
    public Semester createSemester(int year, SemesterType semesterType) throws SemesterException {
        if (semesterExists(semesterType.name() + year))
            throw new SemesterException("Semester already exists");
        Semester semester = new Semester(year, semesterType);
        semesterRepository.save(semester);
        return semester;
    }

    @Transactional
    public Optional<Semester> getSemesterByCode(String code){
        return semesterRepository.findSemesterByCode(code);
    }

    @Transactional
    public boolean semesterExists(String code){
        return getSemesterByCode(code).isPresent();
    }

    @Transactional
    public Optional<Semester> getActiveSemester(){
        return semesterRepository.findSemesterByIsActiveIsTrue();
    }

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

    @Transactional
    public List<Semester> getAllSemesters(){
        return semesterRepository.findAll();
    }

    @Transactional
    public Classroom createClassroom(String code, int capacity) throws ClassroomException {
        if (classroomExists(code)) throw new ClassroomException("Classroom with such code already exists");
        if (capacity < 1 || capacity > 200) throw new ClassroomException("Classroom capacity must be 1-200.");
        Classroom classroom = new Classroom(code, capacity);
        classroomRepository.save(classroom);
        return classroom;
    }

    @Transactional
    public Optional<Classroom> getClassroomByCode(String code){
        return classroomRepository.findClassroomByCode(code);
    }

    @Transactional
    public boolean classroomExists(String code){
        return getClassroomByCode(code).isPresent();
    }

    @Transactional
    public List<Classroom> getAllClassrooms(){
        return classroomRepository.findAll();
    }
}

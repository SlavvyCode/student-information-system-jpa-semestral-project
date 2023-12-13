package cz.cvut.fel.ear.sis.rest;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.rest.handler.utils.RestUtils;
import cz.cvut.fel.ear.sis.service.AdminService;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.exception.ClassroomException;
import cz.cvut.fel.ear.sis.utils.exception.SemesterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/admin")
public class AdminController {
    private final AdminService adminService;
    @Autowired
    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @GetMapping(value = "/room", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Classroom> getClassrooms(){
        return adminService.getAllClassrooms();
    }

    @PostMapping(value = "/classroom", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createClassroom(@RequestBody Classroom classroom) throws ClassroomException {
        adminService.createClassroom(classroom.getCode(), classroom.getCapacity());
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{code}", classroom.getCode());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(value = "/classroom/{code}")
    public Classroom getClassroomByCode(@PathVariable String code) throws ClassroomException {
        final Optional<Classroom> classroom = adminService.getClassroomByCode(code);
        if(classroom.isEmpty()){
            throw new ClassroomException("Classroom not found.");
        }
        return classroom.get();
    }

    @GetMapping(value = "/semester", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Semester> getSemesters(){
        return adminService.getAllSemesters();
    }

    @PostMapping(value = "/semester", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createSemester(@RequestBody Semester semester) throws SemesterException {
        adminService.createSemester(semester.getStartDate().getYear(), semester.getSemesterType());
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{code}", semester.getCode());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(value = "/semester/{code}")
    public Semester getSemesterByCode(@PathVariable String code) throws SemesterException {
        final Optional<Semester> semester = adminService.getSemesterByCode(code);
        if(semester.isEmpty()){
            throw new SemesterException("Semester not found.");
        }
        return semester.get();
    }

    @PatchMapping(value = "/semester/{code}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createSemester(@PathVariable String code) throws SemesterException {
        Optional<Semester> semester = adminService.getSemesterByCode(code);
        if(semester.isEmpty()){
            throw new SemesterException("Semester not found.");
        }
        adminService.setActiveSemester(semester.get());
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{code}", semester.get().getCode());
        return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    }


}

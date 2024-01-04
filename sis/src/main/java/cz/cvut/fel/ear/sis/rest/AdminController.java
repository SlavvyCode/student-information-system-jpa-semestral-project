package cz.cvut.fel.ear.sis.rest;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.rest.dto.CreateClassroomRequestBody;
import cz.cvut.fel.ear.sis.rest.dto.CreateSemesterRequestBody;
import cz.cvut.fel.ear.sis.rest.handler.utils.RestUtils;
import cz.cvut.fel.ear.sis.service.AdminService;
import cz.cvut.fel.ear.sis.utils.exception.ClassroomException;
import cz.cvut.fel.ear.sis.utils.exception.SemesterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    /**
     * Endpoint to retrieve all classrooms.
     *
     * @return List of all classrooms.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/room", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Classroom> getClassrooms(){
        return adminService.getAllClassrooms();
    }

    /**
     * Endpoint to create a new classroom.
     *
     * @param body The request body containing details to create a new classroom.
     * @return ResponseEntity with a location header indicating successful creation or error.
     * @throws ClassroomException If there's an issue creating the classroom.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/classroom", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createClassroom(@RequestBody CreateClassroomRequestBody body) throws ClassroomException {
        Classroom classroom = adminService.createClassroom(body.code, body.capacity);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{code}", classroom.getCode());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve a classroom by its code.
     *
     * @param code The unique code identifying the classroom.
     * @return The classroom corresponding to the provided code.
     * @throws ClassroomException If the classroom with the provided code is not found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/classroom/{code}")
    public Classroom getClassroomByCode(@PathVariable String code) throws ClassroomException {
        final Optional<Classroom> classroom = adminService.getClassroomByCode(code);
        if(classroom.isEmpty()){
            throw new ClassroomException("Classroom not found.");
        }
        return classroom.get();
    }

    /**
     * Endpoint to retrieve all semesters.
     *
     * @return List of all semesters.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/semester", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Semester> getSemesters(){
        return adminService.getAllSemesters();
    }

    /**
     * Endpoint to create a new semester.
     *
     * @param body The request body containing details to create a new semester.
     * @return ResponseEntity with a location header indicating successful creation or error.
     * @throws SemesterException If there's an issue creating the semester.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/semester", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createSemester(@RequestBody CreateSemesterRequestBody body) throws SemesterException {
        Semester semester = adminService.createSemester(body.year, body.semesterType);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{code}", semester.getCode());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve a semester by its code.
     *
     * @param code The unique code identifying the semester.
     * @return The semester corresponding to the provided code.
     * @throws SemesterException If the semester with the provided code is not found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/semester/{code}")
    public Semester getSemesterByCode(@PathVariable String code) throws SemesterException {
        final Optional<Semester> semester = adminService.getSemesterByCode(code);
        if(semester.isEmpty()){
            throw new SemesterException("Semester not found.");
        }
        return semester.get();
    }

    /**
     * Endpoint to set an active semester based on its code.
     *
     * @param code The unique code identifying the semester.
     * @return ResponseEntity with a location header indicating the operation success.
     * @throws SemesterException If the semester with the provided code is not found.
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping(value = "/semester/{code}")
    public ResponseEntity<Void> setActiveSemester(@PathVariable String code) throws SemesterException {
        Optional<Semester> semester = adminService.getSemesterByCode(code);
        if(semester.isEmpty()){
            throw new SemesterException("Semester not found.");
        }
        adminService.setActiveSemester(semester.get());
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
        final HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);
        return new ResponseEntity<>(headers, HttpStatus.ACCEPTED);
    }


}

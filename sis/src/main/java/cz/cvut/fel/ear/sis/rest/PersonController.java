package cz.cvut.fel.ear.sis.rest;

import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.rest.dto.CreatePersonRequestBody;
import cz.cvut.fel.ear.sis.rest.handler.utils.RestUtils;
import cz.cvut.fel.ear.sis.service.AdminService;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/rest/person")
public class PersonController {
    //register and login

//    private final PersonService personService;
//    @Autowired
//    public PersonController(PersonService personService){
//        this.personService = personService;
//    }
//
//
//
//    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Void> register(@RequestBody CreatePersonRequestBody body) throws PersonException {
//
//        Person person = personService.createANewPerson(body.firstName, body.lastName, body.email, body.phoneNumber, body.birthDate, body.password, body.roleKeypass);
//
//        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", person.getId());
//
//        return new ResponseEntity<>(headers, HttpStatus.CREATED);
//    }


}

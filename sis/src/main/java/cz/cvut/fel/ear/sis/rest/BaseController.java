package cz.cvut.fel.ear.sis.rest;

import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.rest.dto.CreatePersonRequestBody;
import cz.cvut.fel.ear.sis.rest.handler.utils.RestUtils;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/person")
public class BaseController {

    private final PersonService personService;

    @Autowired
    public BaseController(PersonService personService){this.personService = personService;}

    /**
     * Endpoint to register a new person.
     *
     * @param body The request body containing details to create a new person.
     * @return ResponseEntity with a status code indicating successful creation or error.
     * @throws PersonException If there's an issue creating the person.
     */
    @PreAuthorize("isAnonymous()")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody CreatePersonRequestBody body) throws PersonException {
        personService.createANewPerson(
                body.firstName,
                body.lastName,
                body.email,
                body.phoneNumber,
                body.birthDate,
                body.password,
                body.roleKeypass
        );
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}

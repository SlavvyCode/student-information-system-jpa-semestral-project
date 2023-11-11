package cz.cvut.fel.ear.sis;

import cz.cvut.fel.ear.sis.dao.PersonRepository;
import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.model.Student;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class SisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SisApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(PersonRepository repository){
        return (args -> {
            insertStudents(repository);
            insertPeople(repository);
            System.out.println(repository.findAll());
        });
    }
    public void insertPeople(PersonRepository repository) {
        repository.save(new Person("First", "Last", "m@m.cz", "1", LocalDate.now()));
    }

    public void insertStudents(PersonRepository repository) {
        repository.save(new Student("First", "Last", "m@m.cz", "1", LocalDate.now()));
    }
}

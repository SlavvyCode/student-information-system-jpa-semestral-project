package cz.cvut.fel.ear.sis;

import cz.cvut.fel.ear.sis.dao.CourseRepository;
import cz.cvut.fel.ear.sis.dao.PersonRepository;
import cz.cvut.fel.ear.sis.model.Course;
import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.model.Student;
import cz.cvut.fel.ear.sis.model.Teacher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Locale;

@SpringBootApplication
public class SisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SisApplication.class, args);
    }


    private Teacher testTeacher;

    @Bean
    public CommandLineRunner run(PersonRepository repository, CourseRepository courseRepository){
        return (args -> {
            insertStudents(repository);
            insertTeachers(repository);
            insertPeople(repository);

            //find all teachers

            insertCourses(courseRepository);


            System.out.println(repository.findAll());
        });
    }
    public void insertPeople(PersonRepository repository) {
        repository.save(new Person("First", "Last", "m@m.cz", "1", LocalDate.now()));
    }

    public void insertStudents(PersonRepository repository) {
        repository.save(new Student("First", "Last", "m@m.cz", "1", LocalDate.now()));
    }

    public void insertTeachers(PersonRepository repository) {


        testTeacher = new Teacher("First", "Last", "m@m.cz", "1", LocalDate.now());

        repository.save(testTeacher);
    }

    //new course
    public void insertCourses(CourseRepository repository) {
        repository.save(new Course("Coursename", 1, Locale.ENGLISH, testTeacher));

    }



}

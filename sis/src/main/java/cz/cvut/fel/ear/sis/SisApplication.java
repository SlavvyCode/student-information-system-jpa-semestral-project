package cz.cvut.fel.ear.sis;

import cz.cvut.fel.ear.sis.dao.CourseRepository;
import cz.cvut.fel.ear.sis.dao.PersonRepository;
import cz.cvut.fel.ear.sis.model.Teacher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SisApplication.class, args);
    }


    private Teacher testTeacher;

    @Bean
    public CommandLineRunner run(PersonRepository repository, CourseRepository courseRepository){
        return (args -> {
//            insertStudents(repository);
//            insertTeachers(repository);
//            insertPeople(repository);
//
//            //find all teachers
//
//            insertCourses(courseRepository);
//
//
//            System.out.println(repository.findAll());
        });
    }
//    public void insertPeople(UserRepository repository) {
//        repository.save(new User("First", "Last", "m@m.cz", "1", LocalDate.now()));
//    }
//
//    public void insertStudents(UserRepository repository) {
//        repository.save(new Student("First", "Last", "m@m.cz", "1", LocalDate.now()));
//    }
//
//    public void insertTeachers(UserRepository repository) {
//
//
//        testTeacher = new Teacher("First", "Last", "m@m.cz", "1", LocalDate.now());
//
//        repository.save(testTeacher);
//    }
//
//    //new course
//    public void insertCourses(CourseRepository repository) {
//        repository.save(new Course("Coursename", 1, Locale.ENGLISH, testTeacher));
//
//    }



}

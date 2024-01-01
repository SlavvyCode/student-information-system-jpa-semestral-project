package cz.cvut.fel.ear.sis;

import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class SisApplication {

    PersonService personService;

    public static void main(String[] args) {
        SpringApplication.run(SisApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(){
        return (args -> {
        });
    }


}

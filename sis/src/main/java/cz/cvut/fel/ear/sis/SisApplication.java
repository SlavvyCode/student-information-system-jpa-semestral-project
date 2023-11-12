package cz.cvut.fel.ear.sis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SisApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(){
        return (args -> {
        });
    }


}

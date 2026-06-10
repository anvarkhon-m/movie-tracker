package com.movietracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MovieTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieTrackerApplication.class, args);
    }

}

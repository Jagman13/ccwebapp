package com.csye6225.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages="com.csye6225.lms.dao")
@EntityScan(basePackages="com.csye6225.lms.pojo")
public class LibrarymanagementsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibrarymanagementsystemApplication.class, args);
    }

}

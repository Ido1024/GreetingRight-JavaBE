package org.example.greetingright;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GreetingRightApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreetingRightApplication.class, args);
    }
    //todo make sure to delete the token after logout.
    //todo refactor name. function \ classes
    //todo remove the logger stuff- for debug
    //todo add roles (also, when creating automatic is USER role. also adminPage)
    //todo when logout \ refresh-token called. delete the row in the DB. Where is the access token saved?
 }

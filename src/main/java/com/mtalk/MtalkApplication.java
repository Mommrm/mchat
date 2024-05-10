package com.mtalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MtalkApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtalkApplication.class, args);
    }

}

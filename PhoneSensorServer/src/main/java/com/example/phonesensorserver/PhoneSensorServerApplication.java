package com.example.phonesensorserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@EnableScheduling
@SpringBootApplication
public class PhoneSensorServerApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PhoneSensorServerApplication.class);
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        SpringApplication.run(PhoneSensorServerApplication.class, args);
    }

}

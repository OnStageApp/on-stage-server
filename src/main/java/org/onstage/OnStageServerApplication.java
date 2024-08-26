package org.onstage;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@RequiredArgsConstructor
@SpringBootApplication
@EnableMongoRepositories
@EnableScheduling
public class OnStageServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnStageServerApplication.class, args);
    }
}

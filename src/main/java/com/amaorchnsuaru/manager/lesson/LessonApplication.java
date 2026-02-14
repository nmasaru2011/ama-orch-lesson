package com.amaorchnsuaru.manager.lesson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan("com.amaorchnsuaru.manager")
@EntityScan("com.amaorchnsuaru.manager.entity")
@EnableJpaRepositories("com.amaorchnsuaru.manager.repository")
public class LessonApplication {

	public static void main(String[] args) {
		SpringApplication.run(LessonApplication.class, args);
	}

}

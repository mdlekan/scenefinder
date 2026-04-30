package com.mikelekan.scenefinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class ScenefinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScenefinderApplication.class, args);
	}

}

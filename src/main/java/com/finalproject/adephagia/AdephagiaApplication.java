package com.finalproject.adephagia;

import com.finalproject.adephagia.service.IngestionService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AdephagiaApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context =SpringApplication.run(AdephagiaApplication.class, args);
		context.getBean(IngestionService.class).ingest();
	}

}

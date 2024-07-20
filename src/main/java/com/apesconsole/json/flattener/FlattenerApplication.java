package com.apesconsole.json.flattener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlattenerApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FlattenerApplication.class, args);
	}
	
	@Autowired
	JsonToEDI jsonToEdi;

	@Override
	public void run(String... args) throws Exception {
		jsonToEdi.generateEdi();
	}

}

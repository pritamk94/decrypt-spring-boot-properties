package com.springboot.prj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DecryptSpringBootPropertiesApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DecryptSpringBootPropertiesApplication.class);
		app.addListeners(new CustomDecryptListener());
		app.run(args);
	}

}

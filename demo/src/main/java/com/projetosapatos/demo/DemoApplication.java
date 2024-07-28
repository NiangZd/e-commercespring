package com.projetosapatos.demo;

import com.projetosapatos.demo.aplicacao.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	Sapato s = new Sapato();

}

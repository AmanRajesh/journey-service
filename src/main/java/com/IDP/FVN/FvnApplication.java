package com.IDP.FVN;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class FvnApplication {

	public static void main(String[] args) {
		SpringApplication.run(FvnApplication.class, args);
	}

}

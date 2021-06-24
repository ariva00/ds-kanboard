package com.pollofritto.dskanboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pollofritto.persistence.FileStorageHandler;

@SpringBootApplication
public class DsKanboardApplication {

	private static FileStorageHandler fileStorageHandler;
	
	public static void main(String[] args) {
		SpringApplication.run(DsKanboardApplication.class, args);
	}

	public static FileStorageHandler getFileStorageHandler() {
		return fileStorageHandler;
	}
	
}

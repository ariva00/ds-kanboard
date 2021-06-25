package com.pollofritto.dskanboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pollofritto.persistence.FileStorageManager;
import com.pollofritto.persistence.FileSystemFileStorageManager;

@SpringBootApplication
public class DsKanboardApplication {

	private static FileStorageManager fileStorageHandler;
	
	public static void main(String[] args) {
		String fileSeparator = System.getProperty("file.separator");
		String userHome = System.getProperty("user.home");
		String directory = "ds-kanboard";
		
		fileStorageHandler = new FileSystemFileStorageManager(userHome + fileSeparator + directory + fileSeparator);
		SpringApplication.run(DsKanboardApplication.class, args);
	}

	public static FileStorageManager getFileStorageHandler() {
		return fileStorageHandler;
	}
	
}

package com.pollofritto.dskanboard;

import com.pollofritto.model.DataManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pollofritto.persistence.FileStorageManager;
import com.pollofritto.persistence.FileSystemDataPersistenceManager;
import com.pollofritto.persistence.FileSystemFileStorageManager;

/**
 * 
 * {@link SpringBootApplication} presenting a kanban board API and a web application to interact with it
 *
 */
@SpringBootApplication
public class DsKanboardApplication {

	private static FileStorageManager fileStorageHandler;
	private static DataManager dataManager;
	
	public static void main(String[] args) {
		String fileSeparator = System.getProperty("file.separator");
		String userHome = System.getProperty("user.home");
		String directory = "ds-kanboard";
		
		dataManager = new DataManager(new FileSystemDataPersistenceManager(userHome + fileSeparator + directory + fileSeparator + "data" + fileSeparator, "data.dat"));
		fileStorageHandler = new FileSystemFileStorageManager(userHome + fileSeparator + directory + fileSeparator + "uploads" + fileSeparator);
		SpringApplication.run(DsKanboardApplication.class, args);
	}

	public static FileStorageManager getFileStorageHandler() {
		return fileStorageHandler;
	}
	
	public static DataManager getDataManager() {
		return dataManager;
	}
	
}

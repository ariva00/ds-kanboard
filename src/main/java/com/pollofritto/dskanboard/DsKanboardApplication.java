package com.pollofritto.dskanboard;

import com.pollofritto.model.DataManager;

import java.io.File;

import javax.xml.crypto.Data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.pollofritto.persistence.FileStorageManager;
import com.pollofritto.persistence.FileSystemDataPersistenceManager;
import com.pollofritto.persistence.FileSystemFileStorageManager;

/**
 * 
 * {@link SpringBootApplication} presenting a kanban board API and a web application to interact with it.
 *
 */
@SpringBootApplication
public class DsKanboardApplication {

	private static FileStorageManager fileStorageHandler;
	private static DataManager dataManager;
	
	public static void main(String[] args) {
		String fileSeparator = System.getProperty("file.separator");
		String rootDirectory = System.getProperty("user.home");
		String directory = "ds-kanboard";
		
		if (args.length > 0) {
			rootDirectory = args[1];
			new File(rootDirectory).mkdirs();
		}
		
		dataManager = new DataManager(new FileSystemDataPersistenceManager(rootDirectory + fileSeparator + directory + fileSeparator + "data" + fileSeparator, "data.dat"));
		fileStorageHandler = new FileSystemFileStorageManager(rootDirectory + fileSeparator + directory + fileSeparator + "uploads" + fileSeparator);
		SpringApplication.run(DsKanboardApplication.class, args);
	}

	/**
	 * Returns the instance of the {@link FileStorageManager}
	 * @return
	 */
	public static FileStorageManager getFileStorageManager() {
		return fileStorageHandler;
	}
	
	/**
	 * Returns the instance of the {@link Data}
	 * @return
	 */
	public static DataManager getDataManager() {
		return dataManager;
	}
	
}

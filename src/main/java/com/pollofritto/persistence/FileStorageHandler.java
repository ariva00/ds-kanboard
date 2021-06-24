package com.pollofritto.persistence;

import org.springframework.web.multipart.MultipartFile;

/**
 * A class that manages the storing of new files to a public location
 *
 */
public abstract class FileStorageHandler {

	/**
	 * Stores a file to the returned path
	 * @param file
	 * @return path of the file
	 */
	public abstract String storeFile(MultipartFile file);
	
	/**
	 * Stores a file with a given name to the returned path
	 * @param file
	 * @param fileName
	 * @return path of the file
	 */
	public abstract String storeFile(MultipartFile file, String fileName);
	
}

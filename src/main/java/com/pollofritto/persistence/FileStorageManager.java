package com.pollofritto.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * A class that manages the storing of new files to a public location
 *
 */
public abstract class FileStorageManager {

	/**
	 * Stores a file to the returned path
	 * @param file
	 * @return the URI of the given stored file
	 */
	public abstract String storeFile(MultipartFile file) throws IOException;
	
	/**
	 * Stores a file with a given name to the returned path
	 * @param file
	 * @param fileName
	 * @return path of the file
	 */
	public abstract String storeFile(MultipartFile file, String fileName) throws IOException;
	
	/**
	 * Returns the content of the requested file
	 * @param uri of the requested file
	 * @return
	 */
	public abstract byte[] getFile(String uri) throws FileNotFoundException, IOException;

	/**
	 * Stores a new image to the returned path
	 * @param file
	 * @return the URI of the given stored image
	 * @throws IOException
	 */
	public abstract String storeImage(MultipartFile file) throws IOException;

	/**
	 * Stores a new image with the given name to the returned path
	 * @param file
	 * @param fileName
	 * @return the URI of the given stored image
	 * @throws IOException
	 */
	public abstract String storeImage(MultipartFile file, String fileName) throws IOException;
	
}

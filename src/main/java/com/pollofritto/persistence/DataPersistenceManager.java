package com.pollofritto.persistence;

import java.io.IOException;

public abstract class DataPersistenceManager {

	/**
	 * Stores an Object
	 * @param o
	 * @throws IOException
	 */
	public abstract void storeData(Object o) throws IOException;

	/**
	 * Retrieves the Object's data from the storage system
	 * @return The retrieved object
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public abstract Object getData() throws IOException, ClassNotFoundException;
	
}

package com.pollofritto.persistence;

import java.io.IOException;

public abstract class DataPersistenceManager {
	
	public abstract void storeData(Object o) throws IOException;
	
	public abstract Object getData() throws IOException, ClassNotFoundException;
	
}

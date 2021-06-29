package com.pollofritto.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileSystemDataPersistenceManager extends DataPersistenceManager {

	private String path;
	private String filename;
	
	public FileSystemDataPersistenceManager(String path, String filename) {
		File f = new File(path);
		f.mkdirs();
		this.path = path;
		this.filename = filename;
	}
	
	@Override
	public synchronized void storeData(Object o) throws IOException {
		FileOutputStream outStream = new FileOutputStream(new File(path + System.getProperty("file.separator") +filename));
		ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
		objectOutStream.writeObject(o);
		objectOutStream.close();
		outStream.close();
	}

	@Override
	public synchronized Object getData() throws IOException, ClassNotFoundException {
		Object o;
		FileInputStream inStream = new FileInputStream(new File(path + System.getProperty("file.separator") +filename));
		ObjectInputStream objectInputStream = new ObjectInputStream(inStream);
		o = objectInputStream.readObject();
		objectInputStream.close();
		inStream.close();
		return o;
	}

}

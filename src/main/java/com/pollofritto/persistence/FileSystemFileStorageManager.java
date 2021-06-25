package com.pollofritto.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public class FileSystemFileStorageManager extends FileStorageManager {
	
	private String path;
	
	public FileSystemFileStorageManager(String path) {
		File f = new File(path);
		f.mkdirs();
		this.path = path;
	}

	@Override
	public synchronized String storeFile(MultipartFile file) throws IOException {
		return storeFile(file, generateFileName(file));
	}

	@Override
	public synchronized String storeFile(MultipartFile file, String fileName) throws IOException {
		File f = new File(path + fileName);
		
		if(!f.createNewFile()) {
			throw new IOException("File already existing");
		}

		FileOutputStream out = new FileOutputStream(f);
		out.write(file.getBytes());
		out.close();
		return fileName;
	}

	@Override
	public byte[] getFile(String uri) throws FileNotFoundException, IOException {
		File f = new File(path + uri);
		
		FileInputStream in = new FileInputStream(f);
		byte[] body = in.readAllBytes();
		in.close();
		return body;
	}
	
	private String generateFileName(MultipartFile file) {
		String[] splitted = file.getOriginalFilename().split("\\.");
		return System.nanoTime() + "." + splitted[splitted.length - 1];
	}
}

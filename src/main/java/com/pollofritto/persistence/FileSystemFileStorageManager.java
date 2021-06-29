package com.pollofritto.persistence;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;

import javax.imageio.ImageIO;

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
		return generateFileName(file, true);
	}
	
	private String generateFileName(MultipartFile file, boolean extention) {
		String[] splitted = file.getOriginalFilename().split("\\.");
		Instant.now().toString();
		if(extention) {
			return Instant.now().getNano() + "." + splitted[splitted.length - 1];
		}
		else {
			return Instant.now().getNano() + "";
		}
		
		
	}

	@Override
	public String storeImage(MultipartFile file) throws IOException {
		return storeImage(file, generateFileName(file, false));
	}

	@Override
	public String storeImage(MultipartFile file, String fileName) throws IOException {
		File f = new File(path + fileName + ".png");
		if (!f.createNewFile()) {
			throw new IOException("File already existing");
		}
		
		BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
		int scaledWidth = 0, scaledHeight = 0;
		
		if (bufferedImage.getWidth() > bufferedImage.getHeight() && bufferedImage.getWidth() > 900) {
			scaledWidth = 900;
			scaledHeight = (scaledWidth * bufferedImage.getHeight()) / bufferedImage.getWidth();
		} else if (bufferedImage.getHeight() > 900) {
			scaledHeight = 900;
			scaledWidth = (scaledHeight * bufferedImage.getWidth()) / bufferedImage.getHeight();
		} else {
			scaledWidth = bufferedImage.getWidth();
			scaledHeight = bufferedImage.getHeight();
		}
		
		Image i = bufferedImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT);
		BufferedImage result = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
		result.getGraphics().drawImage(i, 0, 0, null);
		ImageIO.write(result, "png", f);
		return fileName + ".png";
	}
}

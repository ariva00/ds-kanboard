package com.pollofritto.dskanboard;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.pollofritto.persistence.FileStorageManager;

/**
 * 
 * {@link RestController} for handling the web application requests
 *
 */
@Controller
public class WebController {
	
	/**
	 * Stores a copy of the received file through the {@link FileStorageManager}
	 * @param file
	 * @return the URI to get the file
	 */
	@PostMapping("/files/add/")
	@ResponseBody
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			String uri = DsKanboardApplication.getFileStorageHandler().storeFile(file);
			return new ResponseEntity<String>(uri, HttpStatus.CREATED);
		} catch (IOException | NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error has occurred");
		}
	}
	
	/**
	 * Stores a copy of the received image after rescaling it through the {@link FileStorageManager}
	 * @param file
	 * @return the URI to get the file
	 */
	@PostMapping("/images/add/")
	@ResponseBody
	public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
		try {
			String uri = DsKanboardApplication.getFileStorageHandler().storeImage(file);
			return new ResponseEntity<String>(uri, HttpStatus.CREATED);
		} catch (IOException | NullPointerException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error has occurred");
		}
	}
	
	/**
	 * Returns the image at the specified URI
	 * @param filename
	 * @return
	 */
	@GetMapping("/images/{filename}")
	@ResponseBody
	public ResponseEntity<byte[]> getImage(@PathVariable("filename") String filename){
		ResponseEntity<byte[]> response;
		HttpHeaders header;
		byte[] body;
		
		try {
			body = DsKanboardApplication.getFileStorageHandler().getFile(filename);
			
			header = new HttpHeaders();
			header.add("Content-Disposition", "attachment; filename=" + filename);
			response = new ResponseEntity<byte[]>(body, header, HttpStatus.OK);
			
		} catch (FileNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, filename + " does not exists");
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error has occurred");
		}
		
		return response;
		
	}
	
	/**
	 * Returns the file at the specified URI
	 * @param filename
	 * @return
	 */
	@GetMapping("/files/{filename}")
	@ResponseBody
	public ResponseEntity<byte[]> getFile(@PathVariable("filename") String filename){
		ResponseEntity<byte[]> response;
		HttpHeaders header;
		byte[] body;
		
		try {
			body = DsKanboardApplication.getFileStorageHandler().getFile(filename);
			
			header = new HttpHeaders();
			header.add("Content-Disposition", "attachment; filename=" + filename);
			response = new ResponseEntity<byte[]>(body, header, HttpStatus.OK);
			
		} catch (FileNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, filename + " does not exists");
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error has occurred");
		}
		
		return response;
		
	}
	
}

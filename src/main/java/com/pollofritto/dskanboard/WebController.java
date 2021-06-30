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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * A Spring Controller that handles requests that are not in the scope of the RESTful API
 *
 */
@Controller
public class WebController {
	
	/**
	 * Handles post requests for file upload
	 * @param file
	 * @return
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

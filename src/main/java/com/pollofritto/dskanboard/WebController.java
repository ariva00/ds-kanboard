package com.pollofritto.dskanboard;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
	@PostMapping("/upload/")
	public String uploadFile(@RequestParam("file") MultipartFile file) {
		return DsKanboardApplication.getFileStorageHandler().storeFile(file);
	}
	
}

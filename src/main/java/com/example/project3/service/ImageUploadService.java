package com.example.project3.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Service
public class ImageUploadService {
	
	@Value("${editor.img}")
	private String imgUrl;
	
	public Map<String,Object> imageSave(MultipartHttpServletRequest request) throws Exception {
		MultipartFile uploadFile = request.getFile("upload");
		Map<String,Object> responseData = new HashMap<>();
		System.out.println(request);
		String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\img";
		UUID uuid = UUID.randomUUID();
		String fileName = uuid + "_" + uploadFile.getOriginalFilename();
		
		File saveFile = new File(projectPath, fileName);
		uploadFile.transferTo(saveFile);
		String ImgUrl = "https://footballmoa.azurewebsites.net/img/" + fileName;
//		ImageResponse response = new ImageResponse("http://localhost:3000/img/" + fileName);
		responseData.put("uploaded", true);
		responseData.put("url", ImgUrl);
		TimeUnit.SECONDS.sleep(3);
		
		return responseData;
	}
	
}

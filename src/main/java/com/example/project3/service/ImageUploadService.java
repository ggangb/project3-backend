package com.example.project3.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;

@Service
public class ImageUploadService {
	
	
	@Value("${azure.storage.connection.string}")
	private String connectionString;
	
	@Value("${azure.storage.container.name}")
	private String containerName;
	
	public Map<String,Object> imageSave(MultipartHttpServletRequest request) throws Exception {
		
		MultipartFile uploadFile = request.getFile("upload");
		
		BlobContainerClient container = new BlobContainerClientBuilder()
				.connectionString(connectionString)
				.containerName(containerName)
				.buildClient();
		BlobClient blob = container.getBlobClient(uploadFile.getOriginalFilename());
		
		blob.upload(uploadFile.getInputStream(), uploadFile.getSize(), true);
		Map<String,Object> responseData = new HashMap<>();
		responseData.put("uploaded", true);
		responseData.put("url", container.getBlobContainerUrl() + "/" + uploadFile.getOriginalFilename());		
		
		
		return responseData;
	}
	
}

package com.example.project3.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "downment")
@Data
public class Downment {

		@Id
		private String id;
		
		private String writer;
		
		private String text;
		
		private String date;
		
		private String postId;
		
		private String upmentId;
	}


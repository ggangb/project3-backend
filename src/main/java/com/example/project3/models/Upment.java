package com.example.project3.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "upment")
@Data
public class Upment {
	@Id
	private String id;
	
	private String writer;
	
	private String text;
	
	private String date;
	
	private String postId;
	
	@DBRef
	private List<Downment> downment;
}

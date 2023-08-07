package com.example.project3.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "comment")
@Data
public class Comment {
	@Id
	private String id;
	
	private String writer;
	
	private String text;
	
	private String date;
	
	private String postId;
	
	@Transient
	public static final String SEQUENCE_NAME = "comment_sequence";
	
	private Long ref;
	
	private int level;

	private int reforder;
	
	private String deleteYn = "N";
	
	
	private String parentnum;
	
	private int childnum;


	

}

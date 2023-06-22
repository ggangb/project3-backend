package com.example.project3.payload.request;

import jakarta.validation.constraints.NotBlank;

public class BoardSaveRequest {
	
	@NotBlank
	private String title;
	@NotBlank
	private String content;
	@NotBlank
	private String username;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	

}

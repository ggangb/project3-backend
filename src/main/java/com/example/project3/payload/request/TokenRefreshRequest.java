package com.example.project3.payload.request;

import jakarta.validation.constraints.NotBlank;

public class TokenRefreshRequest {
	 @NotBlank
	 private String refreshToken;
	 
	 private String username;

	  public String getRefreshToken() {
	    return refreshToken;
	  }

	  public void setRefreshToken(String refreshToken) {
	    this.refreshToken = refreshToken;
	  }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	  
	  
}

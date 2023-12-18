package com.example.project3.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
public class Categories {
	
	@Id
	private String id;
	
	private String name;
	
	@DBRef
	private List<CategoriesSub> subCategories;
	
	private int count;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<CategoriesSub> getSubCategories() {
		return subCategories;
	}
	
	public void setSubCategories(List<CategoriesSub> subCategories) {
		this.subCategories = subCategories;
	}

	

	
	
	

}

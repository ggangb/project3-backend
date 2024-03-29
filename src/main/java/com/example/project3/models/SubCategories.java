package com.example.project3.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subcategories")
public class SubCategories {
	@Id
	private String id;
	
	private String name;
	
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

	@Override
	public String toString() {
		return "SubCategories [id=" + id + ", name=" + name + ", count=" + count + "]";
	}


	
	
	
	
	
}

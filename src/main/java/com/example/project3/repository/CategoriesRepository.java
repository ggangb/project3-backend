package com.example.project3.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.project3.models.Categories;

public interface CategoriesRepository extends MongoRepository<Categories, String>  {
	
	List<Categories> findAll();
	
	
}

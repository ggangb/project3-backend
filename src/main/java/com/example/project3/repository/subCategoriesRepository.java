package com.example.project3.repository;

import org.springframework.data.mongodb.repository.MongoRepository;


import com.example.project3.models.SubCategories;

public interface subCategoriesRepository extends MongoRepository<SubCategories, String> {
	

}

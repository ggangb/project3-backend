package com.example.project3.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.project3.models.Recommend;

public interface RecommendRepository extends MongoRepository<Recommend, String>{
	
	Recommend findByBoardId(Long boardId);
	

}

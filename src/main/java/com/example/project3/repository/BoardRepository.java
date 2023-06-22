package com.example.project3.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.project3.models.Board;

public interface BoardRepository extends MongoRepository<Board, String> {
	List<Board> findAll();
	
}

package com.example.project3.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.project3.models.Downment;

public interface DownmentRepository extends MongoRepository<Downment, String> {
	
	List<Downment> findByUpmentId(String upmentId);
}

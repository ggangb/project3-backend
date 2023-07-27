package com.example.project3.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.project3.models.Upment;

public interface CommentRepository extends MongoRepository<Upment, String> {
	
	List<Upment> findByPostId(String postId);

	Page<Upment> findAllBypostId(String postId, Pageable pageable);
	
	
}

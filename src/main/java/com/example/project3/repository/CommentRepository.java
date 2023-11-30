package com.example.project3.repository;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.project3.models.Board;
import com.example.project3.models.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
	
		List<Comment>  findAllOrderByReforder(Sort sort);
		
		List<Comment> findByRefOrderByReforderDesc(Long ref);
		
		Page<Comment> findAllByPostId(String postId, Pageable pageable);
		
		void deleteByPostId(String postId);

	
}

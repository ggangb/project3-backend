package com.example.project3.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.example.project3.models.Board;

public interface BoardRepository extends MongoRepository<Board, String> {
	List<Board> findAllByOrderByIdDesc();
	Board findByIdx(Long idx);
	
	@Query(value = "{idx : { $gt : ?0 }}", sort= "{date: 1}")
	List<Board> findNextByIdx(Long idx);
	
	@Query(value = "{idx : { $lt : ?0 }}", sort= "{date: -1}")
	List<Board> findPrevByIdx(Long idx);
	
	List<Board> findAllByOrderByRecommend(Sort sort);
	
}

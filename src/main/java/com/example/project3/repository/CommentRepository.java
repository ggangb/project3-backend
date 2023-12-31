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
	
		
		//댓글그룹목록을 가져오는 메소드
		List<Comment> findByRef(Long ref);
		
		//댓글목록 불러오는 메소드
		Page<Comment> findAllByPostId(String postId, Pageable pageable);
		
		//자식댓글 추가로 refOrder가 바뀌어야할 댓글목록을 가져오는 메소드
		@Query("{'ref': ?0, 'reforder': {$gt: ?1}}")
		List<Comment> updateRefOrder(Long ref, int refOrder);
		
		//부모댓글 그룹의 level 최댓값인 항목 구하는 메소드
		@Query(value = "{'ref': ?0}", sort = "{'level': -1}", fields = "{'level': 1}")
		List<Comment> findMaxLevelByRef(Long ref);
		
		
		//게시글 삭제시 댓글삭제
		void deleteByPostId(String postId);

		List<Comment> existsByText(String input);


		List<Comment> findByWriter(String username);

}

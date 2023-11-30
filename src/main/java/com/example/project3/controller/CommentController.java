package com.example.project3.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project3.models.Board;
import com.example.project3.models.Comment;
import com.example.project3.payload.response.MessageResponse;
import com.example.project3.repository.BoardRepository;
import com.example.project3.repository.CommentRepository;
import com.example.project3.service.CommentService;
import com.example.project3.service.SequenceGeneratorService;

@RestController
@RequestMapping("/api")
public class CommentController {
	
	@Autowired
	CommentService commentService;
	

	
	@PostMapping("/comment")
	public ResponseEntity<?> commentSave(@RequestBody Comment comment) {
		

		if(commentService.commentSave(comment)) {
			return ResponseEntity.ok(new MessageResponse("댓글 등록완료"));
		} else {
			return ResponseEntity.ok(new MessageResponse("댓글 등록실패"));
		}

	}
	
	@GetMapping("/comment/{postId}")
	public Page<Comment> commentList(@PathVariable String postId, 
			@SortDefault.SortDefaults({
				@SortDefault(sort = "ref", direction = Sort.Direction.DESC),
				@SortDefault(sort = "reforder", direction = Sort.Direction.ASC)
				}) Pageable pageable) {
		
		return commentService.getCommentList(postId, pageable);
	}
	
	@PostMapping("/recomment")
	public ResponseEntity<?> reCommentSave(@RequestBody Comment comment) {
		
		commentService.reCommentSave(comment);
		
		return ResponseEntity.ok(new MessageResponse("댓글 등록완료"));
		
	}
	
	@PutMapping("/updaterecomment")
	public ResponseEntity<?> recommentUpdate(@RequestBody Comment comment) {
		commentService.recommentUpdate(comment);
		return ResponseEntity.ok(new MessageResponse("댓글 수정완료"));
	}
	
	@PutMapping("/deletecomment/{contentId}")
	public ResponseEntity<?> commentDelete(@PathVariable String contentId) {
		commentService.recommentDelete(contentId);
		return ResponseEntity.ok(new MessageResponse("댓글 삭제완료"));
	}
	
	




	

}

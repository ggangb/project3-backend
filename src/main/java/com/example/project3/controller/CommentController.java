package com.example.project3.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
import com.example.project3.security.service.SequenceGeneratorService;

@RestController
@RequestMapping("/api")
public class CommentController {
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	SequenceGeneratorService sequenceGeneratorService;
	
	@PostMapping("/comment")
	public ResponseEntity<?> commentSave(@RequestBody Comment comment) {
		

		comment.setRef(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME));
		comment.setChildnum( 0);
		comment.setLevel(0);
		comment.setReforder(0);
		
		
		
		commentRepository.save(comment);
		

		
		return ResponseEntity.ok(new MessageResponse("댓글 등록완료"));
	}
	
	@GetMapping("/comment/{postId}")
	public Page<Comment> commentList(@PathVariable String postId, @PageableDefault(sort = {"ref"},direction = Sort.Direction.DESC ) Pageable pageable) {
		System.out.println(postId);
		System.out.println(pageable);
		Page<Comment> commentAll = commentRepository.findAllByPostId(postId, pageable);
		
		return commentAll;
	}
	
	@PostMapping("/recomment")
	public ResponseEntity<?> reCommentSave(@RequestBody Comment comment) {
		
		System.out.println("입력 대댓글" + comment);
		Optional<Comment> c = commentRepository.findById(comment.getParentnum());
		Comment c1 = c.get();
		System.out.println("부모댓글" + c1);
//		
//			List<Comment> cm = commentRepository.findByRefOrderByReforder(c1.getRef());
//			Comment com = cm.get(0);
//			System.out.println(com);
//			comment.setReforder(com.getReforder());
//			com.setReforder(com.getReforder()+1);
//			c1.setChildnum(c1.getChildnum()+1);
//			comment.setRef(c1.getRef());
//			comment.setLevel(c1.getLevel()+1);
//			System.out.println("댓글등록전 최종" + comment);
//			commentRepository.save(c1);
//			commentRepository.save(com);
//			commentRepository.save(comment);

		
	
		if(c1.getChildnum() == 0) {
			comment.setReforder(1);
			c1.setChildnum((c1.getChildnum()+1));
			comment.setRef(c1.getRef());
			comment.setLevel((c1.getLevel()+1));
			commentRepository.save(c1);
			commentRepository.save(comment);
		} else {
			List<Comment> cm = commentRepository.findAll(Sort.by(Direction.DESC, "reforder"));
			Comment com1 = cm.get(0);
			System.out.println(com1);
			c1.setChildnum(c1.getChildnum()+1);
			comment.setRef(c1.getRef());
			comment.setReforder(com1.getReforder());
			com1.setReforder(com1.getReforder()+1);
			comment.setLevel((c1.getLevel()+1));
			
			
			commentRepository.save(com1);
			commentRepository.save(c1);
			commentRepository.save(comment);
		}
			
		
		return ResponseEntity.ok(new MessageResponse("댓글 등록완료"));
		
	}
	
	@PutMapping("/updaterecomment")
	public ResponseEntity<?> recommentUpdate(@RequestBody Comment comment) {
		commentRepository.save(comment);
		return ResponseEntity.ok(new MessageResponse("댓글 수정완료"));
	}
	
	@PutMapping("/deletecomment/{contentId}")
	public ResponseEntity<?> commentDelete(@PathVariable String contentId) {
		Optional<Comment> c = commentRepository.findById(contentId);
		Comment c1 = c.get();
		c1.setText("[삭제된 댓글입니다.]");
		c1.setDeleteYn("Y");
		commentRepository.save(c1);
		
		return ResponseEntity.ok(new MessageResponse("댓글 삭제완료"));
	}
	
	




	

}

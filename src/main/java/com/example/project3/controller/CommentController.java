package com.example.project3.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project3.models.Board;
import com.example.project3.models.Downment;
import com.example.project3.models.Upment;
import com.example.project3.payload.response.MessageResponse;
import com.example.project3.repository.BoardRepository;
import com.example.project3.repository.CommentRepository;
import com.example.project3.repository.DownmentRepository;

@RestController
@RequestMapping("/api")
public class CommentController {
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private DownmentRepository downmentRepository;
	
	@Autowired
	private BoardRepository boardRepository;

	
	@PostMapping("/upmment")
	public ResponseEntity<?> upmentSave(@RequestBody Upment upment) {
		//System.out.println(upment);
		Optional<Board> board = boardRepository.findById(upment.getPostId());
		//System.out.println(board.get().toString());
		commentRepository.save(upment);
		Board b = board.get();
		List<Upment> upments = commentRepository.findByPostId(upment.getPostId());
		//System.out.println(upments.toString());
		b.setUpment(upments);
		boardRepository.save(b);
		
		return ResponseEntity.ok(new MessageResponse("댓글 등록완료"));
		
	}
	
	@PostMapping("/downment")
	public ResponseEntity<?> downmentSave(@RequestBody Downment downment) {
		System.out.println(downment);
		Optional<Upment> upment = commentRepository.findById(downment.getUpmentId());
		System.out.println(upment.get().toString());
		downmentRepository.save(downment);
		Upment u = upment.get();
		List<Downment> downments = downmentRepository.findByUpmentId(downment.getUpmentId());
		System.out.println(downments.toString());
//		List<Upment> upments = null;
		u.setDownment(downments);
		commentRepository.save(u);
		
		return ResponseEntity.ok(new MessageResponse("댓글 등록완료"));
		
	}
	
	
	@PostMapping("/redownment")
	public ResponseEntity<?> reDownmentSave(@RequestBody Downment downment) {
		System.out.println(downment);
		downmentRepository.save(downment);
//		List<Upment> upments = null;
		
		return ResponseEntity.ok(new MessageResponse("댓글 등록완료"));
		
	}
	
	@GetMapping("/upment/{postId}")
	public Page<Upment> findUpment(@PathVariable String postId, @PageableDefault(sort = {"date"},direction = Sort.Direction.DESC ) Pageable pageable) {
		//System.out.println(pageable);
		//System.out.println(postId);
		Page<Upment> upment = commentRepository.findAllBypostId(postId, pageable);
		//System.out.println(upment.toString());
		
//		query.addCriteria(Criteria.where("postId").regex(".*" + postId + ".*"));
//		query.with(pageable);
//		List<Upment> list = mongoTemplate.find(query, Upment.class);
//		System.out.println(list.toString());
//		Page<Upment> page = commentRepository.findByUpment(postId,pageable);
//		System.out.println(page.toString());
		return upment;
	}
	
	@GetMapping("/downment/{postId}")
	public List<Downment> findDownment(@PathVariable String postId) {
		//System.out.println(postId);
		System.out.println(postId);
		List<Downment> downment = downmentRepository.findByPostId(postId);
		//System.out.println(downment.toString());
		return downment;
	}
}

package com.example.project3.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.project3.models.Board;
import com.example.project3.models.User;
import com.example.project3.payload.request.BoardSaveRequest;
import com.example.project3.payload.response.ImageResponse;
import com.example.project3.payload.response.MessageResponse;
import com.example.project3.repository.BoardRepository;
import com.example.project3.repository.CommentRepository;
import com.example.project3.security.service.SequenceGeneratorService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/api")
@RestController
public class BoardController {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	SequenceGeneratorService sequenceGeneratorService;
	
	@GetMapping("/board")
	public Page<Board> findAll(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable) {
		System.out.println(pageable);
		Page<Board> page = boardRepository.findAll(pageable);
		return page;
	}
	@GetMapping("api//board")
	public Page<Board> home(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable) {
		System.out.println(pageable);
		Page<Board> page = boardRepository.findAll(pageable);
		return page;
	}
	
	@PostMapping("/board")
	public ResponseEntity<?> boardSave(@RequestBody BoardSaveRequest boardSave) {
		
		Board board = new Board(boardSave.getTitle(),
								boardSave.getContent(),
								boardSave.getUsername(),
								boardSave.getDate());
		
		board.setIdx(sequenceGeneratorService.generateSequence(Board.SEQUENCE_NAME));
		System.out.println(board);
		boardRepository.save(board);
		
		return ResponseEntity.ok(new MessageResponse("글 등록완료"));
	}
	
	@GetMapping("/getcontent/{contentId}")
	public ResponseEntity<Board> getContent(@PathVariable Long contentId, 
							HttpServletRequest request,
							HttpServletResponse response) {
		Cookie oldCookie = null;
		Cookie[] cookies = request.getCookies();
		Board board = boardRepository.findByIdx(contentId);
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("viewCount")) {
					oldCookie = cookie;
				}
			}
		}
		
		if(oldCookie != null) {
			if(!oldCookie.getValue().contains("[" + contentId + "]")) {
				board.setView(board.getView()+1);
				boardRepository.save(board);
				oldCookie.setValue(oldCookie.getValue() + "_[" + contentId + "]");
				oldCookie.setPath("/");
				oldCookie.setMaxAge(60 * 60 * 24);
				response.addCookie(oldCookie);
				System.out.println(response);
			}
		} else {
			board.setView(board.getView()+1);
			boardRepository.save(board);
			Cookie newCookie = new Cookie("viewCount","[" + contentId + "]");
	        newCookie.setPath("/");
	        newCookie.setMaxAge(60 * 60 * 24);
	        response.addCookie(newCookie);
	        System.out.println(response.getHeader("Set-Cookie"));
		}
		
//		System.out.println(contentId);
//		Board board = boardRepository.findByIdx(contentId);
//		board.setView(board.getView()+1);
//		boardRepository.save(board);
		try {
			List<Board> boardPrev = boardRepository.findPrevByIdx(contentId);
			board.setPrev(boardPrev.get(0).getIdx());
			System.out.println("prev : " + boardPrev.get(0).getIdx());
		}catch(IndexOutOfBoundsException e) {
			System.out.println("마지막 글 입니다.");
		} 
		try {
			List<Board> boardNext = boardRepository.findNextByIdx(contentId);
			board.setNext(boardNext.get(0).getIdx());
			System.out.println("next : " + boardNext.get(0).getIdx());
		}catch(IndexOutOfBoundsException e) {
			System.out.println("첫번째 글 입니다.");
		} 
		
		return ResponseEntity.ok().body(board);
		
	}
	@GetMapping("/recommend/{contentId}")
	public void recommendContent(@PathVariable Long contentId) {
		Board board = boardRepository.findByIdx(contentId);
		board.setRecommend(board.getRecommend()+1);
		boardRepository.save(board);
	}
	
	@GetMapping("/getrank")
	public List<Board> recommendRank() {
		List<Board> rankPage = boardRepository.findAllByOrderByRecommend(Sort.by(Direction.DESC, "recommend"));
		
		return rankPage;
	}
	
	
	@PostMapping("/imageupload")
	public ImageResponse imageSave(MultipartHttpServletRequest request) throws Exception {
		MultipartFile uploadFile = request.getFile("upload");
		String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\img";
		UUID uuid = UUID.randomUUID();
		String fileName = uuid + "_" + uploadFile.getOriginalFilename();
		
		File saveFile = new File(projectPath, fileName);
		uploadFile.transferTo(saveFile);
		System.out.println(saveFile.toString());
		System.out.println(saveFile.toURL());
		System.out.println(saveFile.toURI());
		System.out.println(saveFile.toPath());
		ImageResponse response = new ImageResponse("http://localhost:3000/img/" + fileName);
		

		return response;
	}
	
	@DeleteMapping("/deleteboard/{postId}")
	public ResponseEntity<?> boardDelete(@PathVariable String postId) {
		System.out.println(postId);
		boardRepository.deleteById(postId);
		commentRepository.deleteByPostId(postId);
		
		return ResponseEntity.ok(new MessageResponse("글 삭제완료"));
	}
	
	@PutMapping("/updateboard")
	public ResponseEntity<?> boardUpdate(@RequestBody Board board) {
		System.out.println(board);
		boardRepository.save(board);
		
		return ResponseEntity.ok(new MessageResponse("글 수정완료"));
	}
	
}

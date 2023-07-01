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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.project3.models.Board;
import com.example.project3.payload.request.BoardSaveRequest;
import com.example.project3.payload.response.MessageResponse;
import com.example.project3.repository.BoardRepository;

@RestController
@RequestMapping("/api")
public class BoardController {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@GetMapping("/board")
	public Page<Board> findAll(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable) {
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
		System.out.println(board);
		boardRepository.save(board);
		
		return ResponseEntity.ok(new MessageResponse("글 등록완료"));
	}
	
	@GetMapping("/getcontent/{contentId}")
	public Optional<Board> getContent(@PathVariable String contentId) {
		System.out.println(contentId);
		
		
		return boardRepository.findById(contentId);
		
	}
	
	@PostMapping("/imageupload")
	public File imageSave(MultipartHttpServletRequest request) throws Exception {
		MultipartFile uploadFile = request.getFile("upload");
		String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\img";
		UUID uuid = UUID.randomUUID();
		
		String fileName = uuid + "_" + uploadFile.getOriginalFilename();
		
		File saveFile = new File(projectPath, fileName);
		
		uploadFile.transferTo(saveFile);
		
		System.out.println(saveFile);
		
		return saveFile;
	}
	
}

package com.example.project3.controller;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	public List<Board> findAll() {
		
		return boardRepository.findAll();
	}
	
	@PostMapping("/board")
	public ResponseEntity<?> boardSave(@RequestBody BoardSaveRequest boardSave) {
		Board board = new Board(boardSave.getTitle(),
								boardSave.getContent(),
								boardSave.getUsername());
				
		boardRepository.save(board);
		
		return ResponseEntity.ok(new MessageResponse("글 등록완료"));
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

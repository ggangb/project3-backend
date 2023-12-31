package com.example.project3.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import com.example.project3.models.Categories;
import com.example.project3.models.Recommend;
import com.example.project3.models.User;
import com.example.project3.payload.request.BoardSaveRequest;
import com.example.project3.payload.response.ImageResponse;
import com.example.project3.payload.response.MessageResponse;
import com.example.project3.repository.BoardRepository;
import com.example.project3.repository.CommentRepository;
import com.example.project3.service.Boardservice;
import com.example.project3.service.ImageUploadService;
import com.example.project3.service.SequenceGeneratorService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/api")
@RestController
public class BoardController {
	
	@Autowired
	private Boardservice boardService;
	
	@Autowired
	private ImageUploadService imageUploadService;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	SequenceGeneratorService sequenceGeneratorService;
	
	@GetMapping("/board")
	public Page<Board> findAll(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable) {
		System.out.println(pageable);
		Page<Board> page = boardService.findAll(pageable);
		return page;
	}
	@GetMapping("api//board")
	public Page<Board> home(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable) {
		System.out.println(pageable);
		Page<Board> page = boardService.findAll(pageable);
		return page;
	}
	
	@GetMapping("/changetab/{categories}")
	public Page<Board> finByTab(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable,
			@PathVariable String categories) {
		System.out.println(pageable+ "," + categories);
		Page<Board> page = boardService.findCategories(pageable, categories);
		
		if (pageable == null) {
	        throw new IllegalArgumentException("결과가 없습니다."); 
	    }
		return page;
		
	}
	@PostMapping("/board")
	public ResponseEntity<?> boardSave(@RequestBody Board boardSave) {
		
	
		int result = boardService.boardSave(boardSave);
		
		if(result != 0) {
			return ResponseEntity.ok(new MessageResponse("글 등록완료"));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("잘못된 요청입니다.");
		}
		
	}
	
	@GetMapping("/getcontent/{contentId}")
	public ResponseEntity<Board> getContent(@PathVariable Long contentId, 
							HttpServletRequest request,
							HttpServletResponse response) {
		Board board = boardService.getContent(contentId, request, response);
		
		return ResponseEntity.ok().body(board);
		
	}
	
	@GetMapping("/mypost") 
	public Page<Board> getMyPost(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam Map<String, Object> postData) {
		String username = (String) postData.get("username");
		String type = (String) postData.get("type");
		System.out.println(username);
		System.out.println(type);

			Page<Board> page = boardService.findMyPost(pageable,postData);

		
		return page;
	}
	
	
	@GetMapping("/searchcontent")
	public Page<Board> searchData(@PageableDefault(page =0, size = 5, sort = { "date" }, direction = Sort.Direction.DESC) Pageable pageable,
			@RequestParam Map<String, Object> searchData) {
        
        return boardService.searchContent(pageable,searchData);
    }
	
	@GetMapping("/gettab")
	public List<Categories> getTab() {
		List<Categories> tab = boardService.getTab();
		return tab;
	}
	
	@PostMapping("/recommend")
	public ResponseEntity<?> recommendContent(@RequestBody Recommend recommend) {
		System.out.println(recommend);
		return boardService.recommendContent(recommend);
	}
	
	@GetMapping("/getrank")
	public List<Board> recommendRank() {
			List<Board> rankPage = boardService.recommendRank();
		return rankPage;
	}
	
	
	@PostMapping("/imageupload")
	public Map<String, Object> imageSave(MultipartHttpServletRequest request) throws Exception {
//		MultipartFile uploadFile = request.getFile("upload");
//		Map<String,Object> responseData = new HashMap<>();
//		System.out.println(request);
//		String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\img";
//		UUID uuid = UUID.randomUUID();
//		String fileName = uuid + "_" + uploadFile.getOriginalFilename();
//		
//		File saveFile = new File(projectPath, fileName);
//		uploadFile.transferTo(saveFile);
//		System.out.println(saveFile.toString());
//		System.out.println(saveFile.toURL());
//		System.out.println(saveFile.toURI());
//		System.out.println(saveFile.toPath());
//		String ImgUrl = "http://localhost:3000/img/" + fileName;
////		ImageResponse response = new ImageResponse("http://localhost:3000/img/" + fileName);
//		responseData.put("uploaded", true);
//		responseData.put("url", ImgUrl);
//		TimeUnit.SECONDS.sleep(3);
		

		return imageUploadService.imageSave(request);
	}
	
	@DeleteMapping("/deleteboard/{postId}")
	public ResponseEntity<?> boardDelete(@PathVariable String postId) {
		boardService.boardDelete(postId);
		
		return ResponseEntity.ok(new MessageResponse("글 삭제완료"));
	}
	
	@PutMapping("/updateboard")
	public ResponseEntity<?> boardUpdate(@RequestBody Board board) {

		boardService.boardUpdate(board);
		return ResponseEntity.ok(new MessageResponse("글 수정완료"));
	}
	
}

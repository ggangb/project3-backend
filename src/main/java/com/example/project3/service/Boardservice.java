package com.example.project3.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import com.example.project3.models.Board;
import com.example.project3.payload.request.BoardSaveRequest;
import com.example.project3.repository.BoardRepository;
import com.example.project3.repository.CommentRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class Boardservice {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	SequenceGeneratorService sequenceGeneratorService;
	
	public Page<Board> findAll(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable) {
		Page<Board> page = boardRepository.findAll(pageable);
		return page;
	}
	
	public int boardSave(BoardSaveRequest boardSave) {
		
		Board board = new Board(boardSave.getTitle(),
				boardSave.getContent(),
				boardSave.getUsername(),
				boardSave.getDate());
		board.setIdx(sequenceGeneratorService.generateSequence(Board.SEQUENCE_NAME));
		boardRepository.save(board);
		if(boardRepository.save(board) != null) {
			return 1;
		} else {
			return 0;
		}
		
	}
	
	public Board getContent(Long contentId,HttpServletRequest request,
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
		return board;
	}
	
	public void recommendContent(Long contentId) {
		Board board = boardRepository.findByIdx(contentId);
		board.setRecommend(board.getRecommend()+1);
		boardRepository.save(board);
	}
	
	public List<Board> recommendRank() {
		return boardRepository.findAllByOrderByRecommend(Sort.by(Direction.DESC, "recommend"));
		
	}
	
	public void boardDelete(String postId) {
		
		boardRepository.deleteById(postId);
		commentRepository.deleteByPostId(postId);
		 
	}
	
	public void boardUpdate(Board board) {
		boardRepository.save(board);
	}


	
	
	
}

package com.example.project3.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import com.example.project3.models.Board;
import com.example.project3.models.Categories;
import com.example.project3.models.SubCategories;
import com.example.project3.repository.BoardRepository;
import com.example.project3.repository.CategoriesRepository;
import com.example.project3.repository.subCategoriesRepository;
import com.example.project3.repository.CommentRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class Boardservice {
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private CategoriesRepository categoriesRespository;
	
	@Autowired
	private subCategoriesRepository subCategoriesRepository;
	
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	SequenceGeneratorService sequenceGeneratorService;
	
	public Page<Board> findAll(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable) {
		Page<Board> page = boardRepository.findAll(pageable);
		return page;
	}
	
	public Page<Board> findCategories(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable, String categories) {
		Page<Board> page = boardRepository.findByCategories(pageable, categories);
		Page<Board> page1 = boardRepository.findBySubCategories(pageable, categories);
		System.out.println(page.get());
		System.out.println(page1.get());
		if(page.isEmpty() && page1.isEmpty()) {
			return null;
		} else if(!page.isEmpty()) {
			return page;
		} else {
			return page1;
		}
	}
	
	public int boardSave(Board boardSave) {
		
		Categories request = boardSave.getCategories();
		if(request.getParentId() != null) {
			
			Optional<Categories> result = categoriesRespository.findById(request.getParentId());
			result.get().setCount(result.get().getCount()+1);
			categoriesRespository.save(result.get());
			Optional<SubCategories> resultSub = subCategoriesRepository.findById(request.getId());
			resultSub.get().setCount(resultSub.get().getCount()+1);
			subCategoriesRepository.save(resultSub.get());
			boardSave.setIdx(sequenceGeneratorService.generateSequence(Board.SEQUENCE_NAME));
			boardSave.setCategories(result.get());
			boardSave.setSubCategories(resultSub.get());
			boardRepository.save(boardSave);
			if(boardRepository.save(boardSave) != null) {
				return 1;
			} else {
				return 0;
			}
		} else {
			Optional<Categories> result = categoriesRespository.findById(request.getId());
			result.get().setCount(result.get().getCount()+1);
			categoriesRespository.save(result.get());
			boardSave.setIdx(sequenceGeneratorService.generateSequence(Board.SEQUENCE_NAME));
			boardRepository.save(boardSave);
			if(boardRepository.save(boardSave) != null) {
				return 1;
			} else {
				return 0;
			}
		}
		
	}
	
	public List<Categories> getTab() {
		List<Categories> result = categoriesRespository.findAll();
		return result;
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
		System.out.println("업데이트 전 : " + board);
		board.setRecommend(board.getRecommend()+1);
		System.out.println(board);
		boardRepository.save(board);
	}
	
	public List<Board> recommendRank() {
		return boardRepository.findTop10ByOrderByRecommendDesc();
		
	}
	
	public void boardDelete(String postId) {
		
		boardRepository.deleteById(postId);
		commentRepository.deleteByPostId(postId);
		 
	}
	
	public void boardUpdate(Board board) {
		boardRepository.save(board);
	}


	
	
	
}

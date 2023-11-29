package com.example.project3.controller;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.project3.models.Board;
import com.example.project3.repository.BoardRepository;
import com.example.project3.service.ApiService;
import com.example.project3.service.Boardservice;

@RestController()
@RequestMapping("/home")
public class HomeController {
	@Autowired
	private Boardservice boardService;
	
	@Autowired
	private ApiService apiService;
	
	@GetMapping("/board")
	public Page<Board> findAll(@PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC) Pageable pageable) {
		
		return boardService.findAll(pageable);
	}
	
	@GetMapping("/news")
	public String news() {
		return apiService.getNews();
	}
	
	@GetMapping("/newstab")
	public String keywordNews(@RequestParam("start") int start,@RequestParam("keyword") String keyword) {
		return apiService.getKeywordNews(start, keyword);
		
	}
	
	@GetMapping("/trans")
	public String trans() {
		return apiService.getTransNews();
	}

	@GetMapping("/table/{league}")
	public String table(@PathVariable String league) throws ParseException {
		return apiService.getLeagueTable(league);
	}
	
	@GetMapping("/season")
	public String season(@RequestParam String league, @RequestParam int season) throws ParseException {
		
		return apiService.getLeagueTable(league, season);
		
	}
	
	@GetMapping("/score/{league}")
	public String score(@PathVariable String league) throws ParseException {
		return apiService.getScoreTable(league);
	}
	
	@GetMapping("/scorers")
	public String scorers(@RequestParam String league, @RequestParam int season) throws ParseException {
		return apiService.getScoreTable(league, season);
	}

}

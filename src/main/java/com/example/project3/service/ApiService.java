package com.example.project3.service;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ApiService {
	
	
	
	public String sendNewsRequest(String keyword,int num) {
		String query = keyword;
		int start = num;
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
		String encode = StandardCharsets.UTF_8.decode(buffer).toString();

		URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com").path("/v1/search/news.json")
				.queryParam("query", encode).queryParam("start", start).encode().build().toUri();

		RestTemplate restTemplate = new RestTemplate();

		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Naver-Client-Id", "8WncpxE9lBzklQhOajm2")
				.header("X-Naver-Client-Secret", "MuNmGX8Dwc").build();
		ResponseEntity<String> result = restTemplate.exchange(req, String.class);
		String datas = result.getBody().replaceAll("&quot;", "")
										.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "")
										.replaceAll("&apos;", "'");
		System.out.println(datas);
		return datas;
	}
	
	
	public String getNews() {
		String query = "EPL";
		int start = 1;
		return sendNewsRequest(query,start);
	}
	
	public String getKeywordNews(int num, String keyword) {
		
		String query = keyword; 
		int start = num;
		
		return sendNewsRequest(query, start);
	}
	
	public String getTransNews() {
		String query = "축구이적설";
		int start = 1;
		return sendNewsRequest(query,start);
	}
	
	///////////////////////////////////////////////////////////////
	public String sendLeagueRequest(String league) {
		URI uri = UriComponentsBuilder
				.fromUriString("https://api.football-data.org/v4/competitions/" + league + "/standings").encode().build()
				.toUri();
		RestTemplate restTemplate = new RestTemplate();

		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Auth-token", "c3094ac91e0345eaa953aaf134884aa8")
				.build();
		ResponseEntity<String> result = restTemplate.exchange(req, String.class);
		
		return result.getBody();
		
	}
	
	public String sendLeagueRequest(String league, int season) {
		if(league.equals("CL")) {
			return sendLeagueRequest(league);
		} else {
		URI uri = UriComponentsBuilder
				.fromUriString("https://api.football-data.org/v4/competitions/" + league + "/standings" + "?season=" + season).encode().build()
				.toUri();
		RestTemplate restTemplate = new RestTemplate();

		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Auth-token", "c3094ac91e0345eaa953aaf134884aa8")
				.build();
		ResponseEntity<String> result = restTemplate.exchange(req, String.class);
		
		return result.getBody();
		}
	}
	
	public String sendScoreRequest(String league) {
		URI uri = UriComponentsBuilder
				.fromUriString("https://api.football-data.org/v4/competitions/" + league + "/scorers").encode().build()
				.toUri();
		RestTemplate restTemplate = new RestTemplate();

		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Auth-token", "c3094ac91e0345eaa953aaf134884aa8")
				.build();
		ResponseEntity<String> result = restTemplate.exchange(req, String.class);
		
		return result.getBody();
	}
	
	public String sendScoreRequest(String league, int season) {
		if(league.equals("CL")) {
			return sendScoreRequest(league);
		} else {
			URI uri = UriComponentsBuilder
					.fromUriString("https://api.football-data.org/v4/competitions/" + league + "/" + "scorers" + "?season=" + season).encode().build()
					.toUri();
			RestTemplate restTemplate = new RestTemplate();

			RequestEntity<Void> req = RequestEntity.get(uri).header("X-Auth-token", "c3094ac91e0345eaa953aaf134884aa8")
					.build();
			ResponseEntity<String> result = restTemplate.exchange(req, String.class);
		
		return result.getBody();
		}
	}
	
	
	public String getLeagueTable(String league) {
		if(league == "") {
			String query = "PL";
			return sendLeagueRequest(query);
		} else {
			return sendLeagueRequest(league);
		}
	}
	
	
	public String getLeagueTable(String league, int season) {
		return sendLeagueRequest(league ,season);
	}
	
	public String getScoreTable(String league) {
		if(league == "") {
			String query = "PL";
			return sendScoreRequest(query);
		} else {
			return sendScoreRequest(league);
		}
	}
	
	public String getScoreTable(String league, int season) {
		return sendScoreRequest(league ,season);
	}
	
	
	
	
	
	
	
}

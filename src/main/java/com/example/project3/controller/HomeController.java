package com.example.project3.controller;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController()
@RequestMapping("/api")
public class HomeController {

	@GetMapping("/news")
	public String news() {
		String query = "EPL";
		ByteBuffer buffer = StandardCharsets.UTF_8.encode(query);
		String encode = StandardCharsets.UTF_8.decode(buffer).toString();

		URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com").path("/v1/search/news.json")
				.queryParam("query", encode).encode().build().toUri();

		RestTemplate restTemplate = new RestTemplate();

		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Naver-Client-Id", "8WncpxE9lBzklQhOajm2")
				.header("X-Naver-Client-Secret", "MuNmGX8Dwc").build();
		ResponseEntity<String> result = restTemplate.exchange(req, String.class);
		String datas = result.getBody().replaceAll("&quot;", "")
										.replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "")
										.replaceAll("&apos;", "'");
		System.out.println(datas);
		return datas;
	}

	@GetMapping("/table")
	public List<Object> table() throws ParseException {
//		String query = "PL";
		String[] query = {"PL","SA","BL1","PD","FL1"};
		List<Object> result = new ArrayList<>(); 
		URI uri ;
		RestTemplate restTemplate = new RestTemplate();
		RequestEntity<Void> req;
		ResponseEntity<Object>	res;
		
		for(String s : query) {
			 uri = UriComponentsBuilder
					.fromUriString("https://api.football-data.org/v4/competitions/" + s + "/standings").encode().build()
					.toUri();
			restTemplate = new RestTemplate();
			req = RequestEntity.get(uri).header("X-Auth-token", "c3094ac91e0345eaa953aaf134884aa8")
					.build();
			res = restTemplate.exchange(req, Object.class);
			result.add(res.getBody());
			 uri = UriComponentsBuilder
					.fromUriString("https://api.football-data.org/v4/competitions/" + s + "/scorers").encode().build()
					.toUri();
			restTemplate = new RestTemplate();
			req = RequestEntity.get(uri).header("X-Auth-token", "c3094ac91e0345eaa953aaf134884aa8")
					.build();
			res = restTemplate.exchange(req, Object.class);
			result.add(res.getBody());
		}

		
//		URI uri = UriComponentsBuilder
//				.fromUriString("https://api.football-data.org/v4/competitions/" + query + "/standings").encode().build()
//				.toUri();
//		RestTemplate restTemplate = new RestTemplate();
//
//		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Auth-token", "c3094ac91e0345eaa953aaf134884aa8")
//				.build();
//		ResponseEntity<String> result = restTemplate.exchange(req, String.class);

		return result;
	}

}

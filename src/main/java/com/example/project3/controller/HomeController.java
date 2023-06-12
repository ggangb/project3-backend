package com.example.project3.controller;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
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
		return datas;
	}

	@GetMapping("/table")
	public String table() {
		String query = "PL";

		URI uri = UriComponentsBuilder
				.fromUriString("https://api.football-data.org/v4/competitions/" + query + "/standings").encode().build()
				.toUri();
		RestTemplate restTemplate = new RestTemplate();

		RequestEntity<Void> req = RequestEntity.get(uri).header("X-Auth-token", "c3094ac91e0345eaa953aaf134884aa8")
				.build();
		ResponseEntity<String> result = restTemplate.exchange(req, String.class);

		return result.getBody();
	}

}

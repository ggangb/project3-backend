package com.example.project3.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "board")
public class Board {
	@Transient
	public static final String SEQUENCE_NAME = "board_sequence";
	@Id
	private String id;
	
	private Long idx;
	
	@DBRef
	private Categories categories;
	
	@DBRef
	private SubCategories subCategories;
	
	private Long prev;
	
	private Long next;
	
	private String title;
	
	private String content;
	
	private String username;
	
	private String date;
	
	private int view = 0;
	
	private int recommend = 0;
	
	
	

	public Board(String title, String content, String username, String date, Categories categories) {
		this.title = title;
		this.content = content;
		this.username = username;
		this.date = date;
		this.categories = categories;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	

	public Long getIdx() {
		return idx;
	}


	public void setIdx(Long idx) {
		this.idx = idx;
	}

	

	public Long getPrev() {
		return prev;
	}


	public void setPrev(Long prev) {
		this.prev = prev;
	}


	public Long getNext() {
		return next;
	}


	public void setNext(Long next) {
		this.next = next;
	}


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getView() {
		return view;
	}

	public void setView(int view) {
		this.view = view;
	}

	public int getRecommend() {
		return recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}


	public Categories getCategories() {
		return categories;
	}


	public void setCategories(Categories categories) {
		this.categories = categories;
	}


	public SubCategories getSubCategories() {
		return subCategories;
	}


	public void setSubCategories(SubCategories subCategories) {
		this.subCategories = subCategories;
	}


	@Override
	public String toString() {
		return "Board [id=" + id + ", idx=" + idx + ", categories=" + categories + ", subCategories=" + subCategories
				+ ", prev=" + prev + ", next=" + next + ", title=" + title + ", content=" + content + ", username="
				+ username + ", date=" + date + ", view=" + view + ", recommend=" + recommend + "]";
	}


	

	

	
	
	
	
	
	
}

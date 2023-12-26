package com.example.project3.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "recommend")
public class Recommend {
	
	@Id
	private String id;
	
	private Long boardId;
	
	private List<String> recommendUserIds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getBoardId() {
		return boardId;
	}

	public void setBoardId(Long boardId) {
		this.boardId = boardId;
	}

	public List<String> getRecommendUserIds() {
		return recommendUserIds;
	}

	public void setRecommendUserIds(List<String> recommendUserIds) {
		this.recommendUserIds = recommendUserIds;
	}

	@Override
	public String toString() {
		return "Recommend [id=" + id + ", boardId=" + boardId + ", recommendUserIds=" + recommendUserIds + "]";
	}
	
	
	
}

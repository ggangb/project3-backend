package com.example.project3.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.example.project3.models.Comment;
import com.example.project3.repository.BoardRepository;
import com.example.project3.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	SequenceGeneratorService sequenceGeneratorService;
	
	//부모댓글저장
	public boolean commentSave(Comment comment) {
		comment.setRef(sequenceGeneratorService.generateSequence(Comment.SEQUENCE_NAME));
		comment.setChildnum( 0);
		comment.setLevel(0);
		comment.setReforder(0);
		commentRepository.save(comment);
		
		
		if(commentRepository.save(comment) != null) {
			return true;
		} else {
			return false;
		}
	}
	
	//댓글목록
	public Page<Comment> getCommentList(String postId, Pageable pageable) {
		
		Page<Comment> commentAll = commentRepository.findAllByPostId(postId, pageable);
		
		return commentAll;
	}
	
	//자식댓글저장
	public void reCommentSave(Comment comment) {
		
		//부모 댓글 데이터 가져오기
		Optional<Comment> c = commentRepository.findById(comment.getParentnum());
		Comment parent = c.get();
		
		Comment child = comment;
		
		//자식댓글 추가에 따른 다른 댓글들의 refOrder값 수정후 자식댓글 refOrder값 가져오는 메소드 
		int refOrderResult = refOrderUpdate(parent);
		
		//리턴이 0 이면 아무것도 하지않음
		if(refOrderResult == 0) {
			return;
		}
		//자식 댓글 저장
		child.setRef(parent.getRef());
		child.setLevel(parent.getLevel()+1);
		child.setReforder(refOrderResult);
		commentRepository.save(child);
		
		
		//부모댓글의 자식수 업데이트
		parent.setChildnum(parent.getChildnum()+1);
		commentRepository.save(parent);
			
		
	}
	
	//댓글 수정
	public void recommentUpdate(Comment comment) {
		commentRepository.save(comment);
	}
	
	//댓글 삭제
	public void recommentDelete(String contentId) {
		Optional<Comment> c = commentRepository.findById(contentId);
		Comment c1 = c.get();
		c1.setText("[삭제된 댓글입니다.]");
		c1.setDeleteYn("Y");
		commentRepository.save(c1);
	}
	
	//자식댓글 refOrder구하는 메소드
	private int refOrderUpdate(Comment comment) {
		int saveLevel = comment.getLevel() + 1;
		int refOrder = comment.getReforder();
		int answerNum = comment.getChildnum();
		Long ref = comment.getRef();
		int answerSum = 0;
		
		//부모댓글 그룹 가져오기
		List<Comment> refList = commentRepository.findByRef(ref);
		
		//부모댓글의 자식수의 합 구하기
		for(int i=0; i<refList.size(); i++) {
			answerSum += refList.get(i).getChildnum();
		}
		Comment commentmaxLevel = commentRepository.findMaxLevelByRef(ref).get(0);
		int maxLevel = commentmaxLevel.getLevel();
		
		//저장할 자식댓글의 step과 부모댓글 그룹내의 최댓값 level의 비교
		if(saveLevel < maxLevel) {
			return answerSum + 1;
		} else if (saveLevel == maxLevel) {
				List<Comment> c = commentRepository.updateRefOrder(ref, refOrder + answerNum);
				if(c != null) {	
					for(int i=0; i<c.size(); i++) {
						c.get(i).setReforder(c.get(i).getReforder()+1);
						commentRepository.save(c.get(i));
					}
					return refOrder + answerNum + 1;
				}
				return refOrder + answerNum + 1;
			
		} else if (saveLevel > maxLevel) {
			List<Comment> c = commentRepository.updateRefOrder(ref, refOrder + answerNum);
				if(c != null) {					
					for(int i=0; i<c.size(); i++) {
						c.get(i).setReforder(c.get(i).getReforder()+1);
						commentRepository.save(c.get(i));
					}
					return refOrder + 1;
				}
				return refOrder + 1;			
		}	
		return 0;
	}
	
}

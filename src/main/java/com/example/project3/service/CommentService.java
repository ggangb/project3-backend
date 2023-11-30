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
	
	public Page<Comment> getCommentList(String postId, Pageable pageable) {
		
		Page<Comment> commentAll = commentRepository.findAllByPostId(postId, pageable);
		
		return commentAll;
	}
	
	public void reCommentSave(Comment comment) {
		
		System.out.println("입력 대댓글" + comment);
		Optional<Comment> c = commentRepository.findById(comment.getParentnum());
		Comment parent = c.get();
		System.out.println("부모댓글" + parent);
		
		Comment child = comment;
		
			

			List<Comment> cm = commentRepository.findByRefOrderByReforderDesc(parent.getRef());
			Comment c1 = cm.get(0);
			if(parent.getReforder() == 0) {
				child.setRef(parent.getRef());
				child.setReforder(c1.getReforder()+1);
				child.setLevel(parent.getLevel()+1);
				parent.setChildnum(parent.getChildnum()+1);
				commentRepository.save(parent);
				commentRepository.save(child);
				commentRepository.save(c1);
			} else {
				child.setRef(parent.getRef());
				child.setReforder(parent.getReforder()+1);
				c1.setReforder(c1.getReforder()+1);
				child.setLevel(parent.getLevel()+1);
				parent.setChildnum(parent.getChildnum()+1);
				commentRepository.save(parent);
				commentRepository.save(child);
				commentRepository.save(c1);
			}
		
		
		
		
//		
//			List<Comment> cm = commentRepository.findByRefOrderByReforder(c1.getRef());
//			Comment com = cm.get(0);
//			System.out.println(com);
//			comment.setReforder(com.getReforder());
//			com.setReforder(com.getReforder()+1);
//			c1.setChildnum(c1.getChildnum()+1);
//			comment.setRef(c1.getRef());
//			comment.setLevel(c1.getLevel()+1);
//			System.out.println("댓글등록전 최종" + comment);
//			commentRepository.save(c1);
//			commentRepository.save(com);
//			commentRepository.save(comment);

		
	
//		if(c1.getChildnum() == 0) {
//			comment.setReforder(1);
//			c1.setChildnum((c1.getChildnum()+1));
//			comment.setRef(c1.getRef());
//			comment.setLevel((c1.getLevel()+1));
//			commentRepository.save(c1);
//			commentRepository.save(comment);
//		} else {
//			List<Comment> cm = commentRepository.findAll(Sort.by(Direction.DESC, "reforder"));
//			Comment com1 = cm.get(0);
//			System.out.println(com1);
//			c1.setChildnum(c1.getChildnum()+1);
//			comment.setRef(c1.getRef());
//			comment.setReforder(com1.getReforder());
//			com1.setReforder(com1.getReforder()+1);
//			comment.setLevel((c1.getLevel()+1));
//			commentRepository.save(com1);
//			commentRepository.save(c1);
//			commentRepository.save(comment);
//		}
		
		
	}
	
	public void recommentUpdate(Comment comment) {
		commentRepository.save(comment);
	}
	
	public void recommentDelete(String contentId) {
		Optional<Comment> c = commentRepository.findById(contentId);
		Comment c1 = c.get();
		c1.setText("[삭제된 댓글입니다.]");
		c1.setDeleteYn("Y");
		commentRepository.save(c1);
	}
	
	
}

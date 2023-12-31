package com.example.project3.service;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.project3.models.User;
import com.example.project3.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmailService emailService;
	
	public boolean checkUser(String username) {
		
		return userRepository.existsByUsername(username);
		
	}
	
	public boolean checkEmail(String email) {
		return userRepository.existsByEmail(email);
	}
	
	public User checkAccount(String email) {
		Optional<User> findAccount = userRepository.findByEmail(email);
		return findAccount.get();
	}
	
	public User findUser(String username) {
		Optional<User> findUser = userRepository.findByUsername(username);
		return findUser.get();
	}
	public boolean changePw(User user) {
		return userRepository.save(user) != null;
	}
	
	public boolean changeEmail(HashMap<String, Object> changeData) {
		String email = (String) changeData.get("email");
		String newEmail = (String) changeData.get("newEmail");
		Optional<User> findAccount = userRepository.findByEmail(email);
		User user = findAccount.get();
		
		user.setEmail(newEmail);
		
		return userRepository.save(user) != null;
	}
	
}

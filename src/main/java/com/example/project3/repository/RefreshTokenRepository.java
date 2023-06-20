package com.example.project3.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.project3.models.RefreshToken;
import com.example.project3.models.User;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);

	int deleteByUser(User user);
}

package com.example.project3.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.project3.models.RefreshToken;
import com.example.project3.repository.RefreshTokenRepository;
import com.example.project3.repository.UserRepository;
import com.example.project3.security.jwt.exception.TokenRefreshException;

@Service
public class RefreshTokenService {
	 @Value("${jwt.token.jwtRefreshExpirationMs}")
	  private Long refreshTokenDurationMs;

	  @Autowired
	  private RefreshTokenRepository refreshTokenRepository;

	  @Autowired
	  private UserRepository userRepository;

	  public Optional<RefreshToken> findByToken(String token) {
	    return refreshTokenRepository.findByToken(token);
	  }

	  public RefreshToken createRefreshToken(String string) {
	    RefreshToken refreshToken = new RefreshToken();

	    refreshToken.setUser(userRepository.findById(string).get());
	    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
	    refreshToken.setToken(UUID.randomUUID().toString());

	    refreshToken = refreshTokenRepository.save(refreshToken);
	    return refreshToken;
	  }

	  public RefreshToken verifyExpiration(RefreshToken token) {
	    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
	      refreshTokenRepository.delete(token);
	      throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
	    }

	    return token;
	  }

	  @Transactional
	  public int deleteByUserId(String string) {
	    return refreshTokenRepository.deleteByUser(userRepository.findById(string).get());
	  }
}

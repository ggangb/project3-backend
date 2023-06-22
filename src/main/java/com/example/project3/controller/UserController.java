package com.example.project3.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project3.models.ERole;
import com.example.project3.models.RefreshToken;
import com.example.project3.models.Role;
import com.example.project3.models.User;
import com.example.project3.payload.request.LoginRequest;
import com.example.project3.payload.request.SignupRequest;
import com.example.project3.payload.request.TokenRefreshRequest;
import com.example.project3.payload.response.JwtResponse;
import com.example.project3.payload.response.MessageResponse;
import com.example.project3.payload.response.TokenRefreshResponse;
import com.example.project3.repository.RoleRepository;
import com.example.project3.repository.UserRepository;
import com.example.project3.security.jwt.JwtUtils;
import com.example.project3.security.jwt.exception.TokenRefreshException;
import com.example.project3.security.service.RefreshTokenService;
import com.example.project3.security.service.UserDetailsImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	RefreshTokenService refreshTokenService;
	
	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();		
		
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
		

		return ResponseEntity.ok(new JwtResponse(jwt, 
												refreshToken.getToken(),
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}
	
	@PostMapping("/refreshtoken")
	  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
	    String requestRefreshToken = request.getRefreshToken();

	    return refreshTokenService.findByToken(requestRefreshToken)
	        .map(refreshTokenService::verifyExpiration)
	        .map(RefreshToken::getUser)
	        .map(user -> {
	          String token = jwtUtils.generateTokenFromUsername(user.getUsername());
	          return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
	        })
	        .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
	            "Refresh token is not in database!"));
	  }

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("이미 사용중인 아이디입니다."));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("이미 사용중인 이메일입니다."));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), 
							 signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()),
							 signUpRequest.getPhone()
							 );

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("회원가입 완료"));
	}
	
	@PostMapping("/signout")
	  public ResponseEntity<?> logoutUser(@RequestBody HashMap<String,Object> user) {
		
		String userId = (String) user.get("id");
	    refreshTokenService.deleteByUserId(userId);
	    return ResponseEntity.ok(new MessageResponse("로그아웃 성공"));
	  }
}

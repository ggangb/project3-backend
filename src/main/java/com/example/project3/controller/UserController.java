package com.example.project3.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

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
import com.example.project3.service.EmailService;
import com.example.project3.service.RefreshTokenService;
import com.example.project3.service.SequenceGeneratorService;
import com.example.project3.service.UserDetailsImpl;
import com.example.project3.service.UserDetailsServiceImpl;
import com.example.project3.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
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
	UserService userService;

	@Autowired
	SequenceGeneratorService sequenceGeneratorService;

	@Autowired
	RefreshTokenService refreshTokenService;

	@Autowired
	UserDetailsServiceImpl userDetailServiceImpl;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	EmailService emailService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		System.out.println(loginRequest.getUsername());
		System.out.println(loginRequest.getPassword());
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		String jwt = jwtUtils.generateJwtToken(authentication);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

		return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
				userDetails.getUsername(), userDetails.getEmail(), userDetails.getPhone(), roles));
	}

	@PostMapping("/refreshtoken")
	public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
		String requestRefreshToken = request.getRefreshToken();

		return refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration)
				.map(RefreshToken::getUser).map(user -> {
					String token = jwtUtils.generateTokenFromUsername(user.getUsername());
					return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
				})
				.orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("이미 사용중인 아이디입니다."));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("이미 사용중인 이메일입니다."));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()), signUpRequest.getPhone());
		user.setIdx(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));

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

	@GetMapping("/chceckuser/{username}")
	public boolean checkUser(@PathVariable String username) {
		System.out.println(username);

		return userService.checkUser(username);
	}

	@GetMapping("/checkemail/{email}")
	public boolean checkEmail(@PathVariable String email) {
		System.out.println(email);

		return userService.checkEmail(email);
	}

	@GetMapping("/findaccount/{email}")
	public boolean findAccount(@PathVariable String email) {
		System.out.println(email);
		User findUser = userService.checkAccount(email);
		String jwt = jwtUtils.generateTokenFromUsername(findUser.getUsername());
		System.out.println(findUser.getUsername());
		System.out.println(findUser.getPassword());
		emailService.sendEmail(jwt, email);

		return true;

	}

	@GetMapping("/verifytoken")
	public boolean verifyToken(@RequestParam("token") String token) {
		System.out.println(token);
		boolean isValid = jwtUtils.validateJwtToken(token);
		System.out.println(isValid);
		if (isValid) {
			// 토큰이 유효한 경우
			return true;
		} else {
			// 토큰이 유효하지 않은 경우
			return false;
		}
	}

	@PostMapping("/changepw")
	public boolean changePw(@RequestBody HashMap<String, Object> changeData) {
		String token = (String) changeData.get("token");
		String username = jwtUtils.getUserNameFromJwtToken(token);
		User newUser = userService.findUser(username);
		String newPw = (String) changeData.get("password");
		System.out.println(token);
		System.out.println(newPw);
		newUser.setPassword(encoder.encode(newPw));
		return userService.changePw(newUser);

	}

	@PostMapping("/changeemail")
	public ResponseEntity<?> changeEmail(@RequestBody HashMap<String, Object> changeData) {
		String email = (String) changeData.get("email");
		String newEmail = (String) changeData.get("newEmail");
		User findUser = userService.checkAccount(email);
		System.out.println(email);
		System.out.println(newEmail);
		userService.changeEmail(changeData);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) userDetailServiceImpl
				.loadUserByUsername(findUser.getUsername());

		String jwt = jwtUtils.generateTokenFromUsername(findUser.getUsername());

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
		
		return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
				userDetails.getUsername(), userDetails.getEmail(), userDetails.getPhone(), roles));
	}

	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser(@RequestBody HashMap<String, Object> user) {

		String userId = (String) user.get("id");
		refreshTokenService.deleteByUserId(userId);
		return ResponseEntity.ok(new MessageResponse("로그아웃 되었습니다."));
	}
}

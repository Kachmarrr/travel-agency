package com.epam.finaltask.service.Implementation;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.exception.BadRequestException;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.enums.Role;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository,
						   UserMapper userMapper,
						   PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDTO register(UserDTO userDTO) {
		log.info("Register user attempt: {}", userDTO);

		if (userDTO == null) {
			log.error("Registration failed: userDTO is null");
			throw new BadRequestException("UserDTO cannot be null");
		}

		String username = userDTO.getUsername();
		if (username == null || username.isBlank()) {
			log.error("Registration failed: username is blank");
			throw new BadRequestException("Username (email) is required");
		}

		if (userRepository.existsByUsername(username)) {
			log.warn("Registration rejected: username already exists, username={}", username);
			throw new IllegalArgumentException("User with username '" + username + "' already exists");
		}

		if (userDTO.getRole() == null) {
			userDTO.setRole(Role.USER);
		}

		User user = userMapper.toUser(userDTO);
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

		User savedUser = userRepository.save(user);
		log.info("User registered successfully: username={}, id={}", savedUser.getUsername(), savedUser.getId());
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		log.info("Update user attempt: username={}", username);

		if (username == null || username.isBlank() || userDTO == null) {
			log.error("Update failed: username or userDTO is null");
			throw new BadRequestException("Username and userDTO are required");
		}

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> {
					log.error("Update failed: user not found, username={}", username);
					return new NotFoundException("User with username '" + username + "' not found");
				});

		if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
			if (userDTO.getPassword().length() < 6) {
				log.warn("Update rejected: password too short, username={}", username);
				throw new IllegalArgumentException("Password must be at least 6 characters");
			}
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		}

		User updatedUser = userRepository.save(user);
		log.info("User updated successfully: username={}, id={}", updatedUser.getUsername(), updatedUser.getId());
		return userMapper.toUserDTO(updatedUser);
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		log.info("Get user by username attempt: {}", username);

		if (username == null || username.isBlank()) {
			log.error("Get user failed: username is null or blank");
			throw new BadRequestException("Username is required");
		}

		UserDTO userDTO = userRepository.findByUsername(username)
				.map(userMapper::toUserDTO)
				.orElseThrow(() -> {
					log.error("Get user failed: user not found, username={}", username);
					return new NotFoundException("User with username '" + username + "' not found");
				});

		log.info("Get user successful: username={}, id={}", username, userDTO.getId());
		return userDTO;
	}

	@Override
	public void changeUserRole(Long userId, Role role) {
		log.info("Change user role attempt: userId={}, role={}", userId, role);

		if (role == null || userId == null) {
			log.error("Change role failed: userId or role is null");
			throw new BadRequestException("Id or role must be required");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> {
					log.error("Change role failed: user not found, userId={}", userId);
					return new IllegalArgumentException("User with " + userId + " not found");
				});

		user.setRole(role);
		userRepository.save(user);

		log.info("User role changed successfully: userId={}, newRole={}", userId, role);
	}

	@Override
	public UserDTO getUserById(Long userId) {
		log.info("Get user by id attempt: {}", userId);

		if (userId == null) {
			log.error("Get user by id failed: userId is null");
			throw new BadRequestException("Id is required");
		}

		UserDTO userDTO = userRepository.findById(userId)
				.map(userMapper::toUserDTO)
				.orElseThrow(() -> {
					log.error("Get user failed: user not found, userId={}", userId);
					return new NotFoundException("User with id " + userId + " not found");
				});

		log.info("Get user by id successful: userId={}, username={}", userId, userDTO.getUsername());
		return userDTO;
	}

	public UserDTO getUserByEmail(String email) {
		log.info("Get user by email attempt: {}", email);

		if (email == null || email.isBlank()) {
			log.error("Get user by email failed: email is null or blank");
			throw new BadRequestException("email is required");
		}

		UserDTO userDTO = userRepository.findUserByEmail(email)
				.map(userMapper::toUserDTO)
				.orElseThrow(() -> {
					log.error("Get user failed: user not found, email={}", email);
					return new NotFoundException("User with " + email + " not found");
				});

		log.info("Get user by email successful: email={}, userId={}", email, userDTO.getId());
		return userDTO;
	}

	@Override
	public List<UserDTO> findAllUsers() {
		return userRepository.findAll().stream()
				.map(userMapper::toUserDTO)
				.toList();
	}
}

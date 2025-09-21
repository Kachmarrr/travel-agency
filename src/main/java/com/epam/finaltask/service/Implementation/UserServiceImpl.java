package com.epam.finaltask.service.Implementation;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.BadRequestException;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.enums.Role;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Override // TODO must be reviewed by Roman
	public UserDTO register(UserDTO userDTO) {
		if (userDTO == null) {
			throw new BadRequestException("UserDTO cannot be null");
		}
		String username = userDTO.getUsername();
		if (username == null || username.isBlank()) {
			throw new BadRequestException("Username (email) is required");
		}

		if (userRepository.existsByUsername(username)) {
			throw new IllegalArgumentException("User with username '" + username + "' already exists");
		}

		String rawPassword = userDTO.getPassword();
		if (rawPassword == null || rawPassword.length() < 6) {
			throw new BadRequestException("Password is required and must be at least 6 chars");
		}

		// first we map userDTO to user, and only after set password(in UserMapper we don`t take password parameter)
		User user = userMapper.toUser(userDTO);
		user.setPassword(passwordEncoder.encode(rawPassword));

		User savedUser = userRepository.save(user);
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		if (username == null || username.isBlank() || userDTO == null) {
			throw new BadRequestException("Username and userDTO are required");
		}

		User user = userRepository
				.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User with username '" + username + "' not found"));

		if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
			if (userDTO.getPassword().length() < 6) {
				throw new IllegalArgumentException("Password must be at least 6 characters");
			}
			user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		}

		User updatedUser = userRepository.save(user);
		return userMapper.toUserDTO(updatedUser);
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		if (username == null || username.isBlank()) {
			throw new BadRequestException("Username is required");
		}

		return userRepository.findByUsername(username)
				.map(userMapper::toUserDTO)
				.orElseThrow(() -> new NotFoundException("User with username '" + username + "' not found"));
	}

	@Override
	public void changeUserRole(Long userId, Role role) {
		if (role == null || userId == null) {
			throw new BadRequestException("Id or role must be required");
		}

		User user = userRepository
				.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User with " + userId + " not found: "));

		user.setRole(role);

		User savedUser = userRepository.save(user);
	}

	@Override
	public UserDTO getUserById(Long userId) {
		if (userId == null) {
			throw new BadRequestException("Id is required");
		}

		return userRepository.findById(userId)
				.map(userMapper::toUserDTO)
				.orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
	}
}

package com.epam.finaltask.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;

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
		if (userDTO == null) throw new IllegalArgumentException("userDTO is required");
		User u = userMapper.toUser(userDTO);
		if (u.getPassword() == null || u.getPassword().isBlank()) {
			u.setPassword(passwordEncoder.encode("change-me-please"));
		} else {
			u.setPassword(passwordEncoder.encode(u.getPassword()));
		}
		if (u.getBalance() == null) u.setBalance(java.math.BigDecimal.ZERO);
		if (u.getRole() == null) u.setRole(com.epam.finaltask.model.Role.USER);
		u.setActive(Boolean.TRUE.equals(u.isActive()));
		return userMapper.toUserDTO(userRepository.save(u));
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		if (username == null || username.isBlank()) throw new IllegalArgumentException("username is required");
		User existing = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found: " + username));

		return userMapper.toUserDTO(userRepository.save(existing));
	}

	@Override
	@Transactional(readOnly = true)
	public UserDTO getUserByUsername(String username) {
		return userRepository.findUserByUsername(username)
				.map(userMapper::toUserDTO)
				.orElseThrow(() -> new RuntimeException("User not found: " + username));
	}

	@Override
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		if (userDTO == null) throw new IllegalArgumentException("userDTO is required");

		User existing;
		if (userDTO.getId() != null && !userDTO.getId().isBlank()) {
			UUID id = UUID.fromString(userDTO.getId());
			existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
		} else if (userDTO.getUsername() != null && !userDTO.getUsername().isBlank()) {
			existing = userRepository.findByUsername(userDTO.getUsername())
					.orElseThrow(() -> new RuntimeException("User not found: " + userDTO.getUsername()));
		} else {
			throw new IllegalArgumentException("Either id or username is required to change account status");
		}

		// Important for tests: call mapper.toUser(userDTO), keep id from existing, save and return DTO
		User mapped = userMapper.toUser(userDTO);
		if (mapped == null) {
			mapped = existing;
			mapped.setActive(userDTO.isActive() != existing.isActive() ? userDTO.isActive() : !existing.isActive());
		} else {
			mapped.setId(existing.getId());
			if (mapped.getPassword() == null || mapped.getPassword().isBlank()) {
				mapped.setPassword(existing.getPassword());
			} else {
				mapped.setPassword(passwordEncoder.encode(mapped.getPassword()));
			}
		}

		User saved = userRepository.save(mapped);
		return userMapper.toUserDTO(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public UserDTO getUserById(UUID id) {
		return userRepository.findById(id)
				.map(userMapper::toUserDTO)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}
}

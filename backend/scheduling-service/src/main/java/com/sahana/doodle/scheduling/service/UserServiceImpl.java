package com.sahana.doodle.scheduling.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sahana.doodle.scheduling.dto.CreateUserRequest;
import com.sahana.doodle.scheduling.dto.UpdateUserRequest;
import com.sahana.doodle.scheduling.dto.UserResponse;
import com.sahana.doodle.scheduling.exception.DuplicateEmailException;
import com.sahana.doodle.scheduling.exception.UserNotFoundException;
import com.sahana.doodle.scheduling.mapper.UserMapper;
import com.sahana.doodle.scheduling.model.User;
import com.sahana.doodle.scheduling.repository.UserRepository;


@Service
@Transactional
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
	}

	@Override
	public UserResponse createUser(CreateUserRequest request) {
		String normalizedEmail = request.email().trim().toLowerCase();

		if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
			throw new DuplicateEmailException(
					"A user already exists with email: " + normalizedEmail
					);
		}

		User user = userMapper.toEntity(request);
		User savedUser = userRepository.save(user);

		return userMapper.toResponse(savedUser);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() ->
				new UserNotFoundException(
						"User not found with id: " + id
						)
						);

		return userMapper.toResponse(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll()
				.stream()
				.map(userMapper::toResponse)
				.toList();
	}

	@Override
	public UserResponse updateUser(Long id, UpdateUserRequest request) {

		User user = userRepository.findById(id)
				.orElseThrow(() ->
				new UserNotFoundException(
						"User not found with id: " + id
						)
						);

		String normalizedEmail = request.email()
				.trim()
				.toLowerCase();

		userRepository.findByEmailIgnoreCase(normalizedEmail)
		.filter(existingUser -> !existingUser.getId().equals(id))
		.ifPresent(existingUser -> {
			throw new DuplicateEmailException(
					"A user already exists with email: " + normalizedEmail
					);
		});

		user.setName(request.name().trim());
		user.setEmail(normalizedEmail);
		user.setTimezone(request.timezone().trim());

		User updatedUser = userRepository.save(user);

		return userMapper.toResponse(updatedUser);
	}

	@Override
	public void deleteUser(Long id) {

		User user = userRepository.findById(id)
				.orElseThrow(() ->
				new UserNotFoundException(
						"User not found with id: " + id
						)
						);

		userRepository.delete(user);
	}
}

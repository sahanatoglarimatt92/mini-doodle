package com.sahana.doodle.scheduling.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sahana.doodle.scheduling.dto.CreateUserRequest;
import com.sahana.doodle.scheduling.dto.UpdateUserRequest;
import com.sahana.doodle.scheduling.dto.UserResponse;
import com.sahana.doodle.scheduling.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService; 

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<UserResponse> createUser(
			@Valid @RequestBody CreateUserRequest request
			) {
		UserResponse response = userService.createUser(request);

		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();

		return ResponseEntity
				.created(location)
				.body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(
			@PathVariable Long id
			) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(
			@PathVariable Long id,
			@Valid @RequestBody UpdateUserRequest request) {

		return ResponseEntity.ok(
				userService.updateUser(id, request)
				);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(
			@PathVariable Long id) {

		userService.deleteUser(id);

		return ResponseEntity.noContent().build();
	}
}

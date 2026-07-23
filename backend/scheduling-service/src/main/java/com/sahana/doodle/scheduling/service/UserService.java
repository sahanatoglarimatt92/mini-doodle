package com.sahana.doodle.scheduling.service;

import java.util.List;

import com.sahana.doodle.scheduling.dto.CreateUserRequest;
import com.sahana.doodle.scheduling.dto.UpdateUserRequest;
import com.sahana.doodle.scheduling.dto.UserResponse;

public interface  UserService {
	UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();
    
    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}

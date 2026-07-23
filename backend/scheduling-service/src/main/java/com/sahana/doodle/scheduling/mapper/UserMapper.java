package com.sahana.doodle.scheduling.mapper;

import org.springframework.stereotype.Component;

import com.sahana.doodle.scheduling.dto.CreateUserRequest;
import com.sahana.doodle.scheduling.dto.UserResponse;
import com.sahana.doodle.scheduling.model.User;

@Component
public class UserMapper {
	 public User toEntity(CreateUserRequest request) {
	        String timezone = request.timezone();

	        if (timezone == null || timezone.isBlank()) {
	            timezone = "UTC";
	        }

	        return new User(
	                request.name().trim(),
	                request.email().trim().toLowerCase(),
	                timezone.trim()
	        );
	    }

	    public UserResponse toResponse(User user) {
	        return new UserResponse(
	                user.getId(),
	                user.getName(),
	                user.getEmail(),
	                user.getTimezone(),
	                user.getCreatedAt(),
	                user.getUpdatedAt()
	        );
	    }
}

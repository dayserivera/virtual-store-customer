package com.techfeense.virtualstorecustomer.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.techfeense.virtualstorecustomer.shared.UserDto;

public interface UsersService extends UserDetailsService {
	UserDto createUser(UserDto userDetails);
	UserDto getUserDetailsByEmail(String email);
	UserDto getUserByUserId(String userId);
}

package com.techfeense.virtualstorecustomer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.techfeense.virtualstorecustomer.data.UserEntity;
import com.techfeense.virtualstorecustomer.data.UserRepository;
import com.techfeense.virtualstorecustomer.shared.UserDto;

@Service
public class UsersServiceImpl implements UsersService {
	String ROLE_PREFIX = "ROLE_";
	
	UserRepository userRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	Environment environment;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	public UsersServiceImpl(UserRepository userRepository, 
			BCryptPasswordEncoder bCryptPasswordEncoder,
			Environment environment) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.environment = environment;
	}

	@Override
	public UserDto createUser(UserDto userDetails) {
		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		
		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
		this.userRepository.save(userEntity);
		
		UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
		
		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(username);
		
		if(userEntity == null) throw new UsernameNotFoundException(username);
		
		List<GrantedAuthority> roles = new ArrayList<>();
		roles.add(new SimpleGrantedAuthority(ROLE_PREFIX + userEntity.getRole()));
		
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, roles);
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		
		if(userEntity == null) throw new UsernameNotFoundException(email);
		
		return new ModelMapper().map(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);
		if(userEntity == null) throw new UsernameNotFoundException("User not found");
		
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
		
		return userDto;
	}

}

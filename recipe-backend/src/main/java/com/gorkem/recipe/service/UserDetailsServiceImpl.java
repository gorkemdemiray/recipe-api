package com.gorkem.recipe.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gorkem.recipe.exception.UserNotFoundException;
import com.gorkem.recipe.model.User;
import com.gorkem.recipe.repository.UserRepository;

/**
 * User details service implementation to be used for authentication.
 * 
 * @author gorkemdemiray
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(String.format("Invalid user name: %s", username)));
		return UserDetailsImpl.build(user);
	}

}

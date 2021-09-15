package com.gorkem.recipe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gorkem.recipe.model.User;

/**
 * User repository
 * 
 * @author gorkemdemiray
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);
	
	Boolean existsByEmail(String email);
}

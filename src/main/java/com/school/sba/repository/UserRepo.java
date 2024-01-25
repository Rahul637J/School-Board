package com.school.sba.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.Users;
import com.school.sba.enums.UserRole;

public interface UserRepo extends JpaRepository<Users, Integer>
{
		boolean existsByUserRole(UserRole role);

		Optional<Users> findByUserName(String username);
}

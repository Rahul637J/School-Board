package com.school.sba.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.sba.entity.Users;

public interface UserRepo extends JpaRepository<Users, Integer>
{

}

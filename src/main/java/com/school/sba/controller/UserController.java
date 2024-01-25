package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.requestdto.UsersRequest;
import com.school.sba.responsedto.UsersResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

import jakarta.validation.Valid;

@RestController
public class UserController 
{
	@Autowired
	private UserService service;
	
	@PostMapping("/users/register/admin")
	public ResponseEntity<ResponseStructure<UsersResponse>> saveAdmin(@RequestBody UsersRequest request)
	{
		return service.saveAdmin(request);
	}
	
	@PostMapping("users/register")   //ONLY ADMIN CAN CREATE THE USERS 
	@PreAuthorize("hasAuthority('hello')")
	public ResponseEntity<ResponseStructure<UsersResponse>> saveUser(@RequestBody @Valid UsersRequest userRequest) {
		return service.saveUser(userRequest);
	}
	
	@GetMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UsersResponse>> getUserById(@PathVariable int userId)
	{
		return service.getUserById(userId);
	}
	
	@PreAuthorize("hasAuthority('ADMIN')")    // USED TO GIVE AUTHORIZATION FOR THIS PARTICULAR METHOD FOR ADMIN
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<ResponseStructure<UsersResponse>> deleteUserById(@PathVariable int userId)
	{
		return service.deleteUserById(userId);
	}
}

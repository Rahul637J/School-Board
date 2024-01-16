package com.school.sba.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@PostMapping("/users/register")
	public ResponseEntity<ResponseStructure<UsersResponse>> saveUser(@RequestBody @Valid UsersRequest request)
	{
		return service.saveUser(request);
	}
	
	@GetMapping(value="/users/{userId}")
	public ResponseEntity<ResponseStructure<UsersResponse>> getUserById(@RequestParam int userId)
	{
		return service.getUserById(userId);
	}
	
	@DeleteMapping(value="/users/{userId}")
	public ResponseEntity<ResponseStructure<UsersResponse>> deleteUserById(@RequestParam int userId)
	{
		return service.deleteUserById(userId);
	}

}

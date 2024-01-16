package com.school.sba.service;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.UsersRequest;
import com.school.sba.responsedto.UsersResponse;
import com.school.sba.util.ResponseStructure;

public interface UserService 
{

	ResponseEntity<ResponseStructure<UsersResponse>> saveUser(UsersRequest request);

	ResponseEntity<ResponseStructure<UsersResponse>> getUserById(int userId);

	ResponseEntity<ResponseStructure<UsersResponse>> deleteUserById(int userId);
	

}

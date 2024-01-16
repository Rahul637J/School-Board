package com.school.sba.serviceimpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Users;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.DuplicateEntryException;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.UsersRequest;
import com.school.sba.responsedto.UsersResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

@Service
public class UsersServiceImpl implements UserService
{
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ResponseStructure<UsersResponse> structure;
	
	static boolean admin=false;
		
	private Users mapToUsers(UsersRequest request)
	{
		return Users.builder()
				.userName(request.getUserName())
				.email(request.getUserEmail())
				.firstName(request.getUserFirstName())
				.lastName(request.getUserLastName())
				.password(request.getUserPassword())
				.contactNo(request.getUserContactNo())
				.userRole(request.getUserRole())
				.build();
	}
	
	private UsersResponse mapToUserResponse(Users response) 
	{
		return UsersResponse.builder()
				.userId(response.getUserId())
				.userName(response.getUserName())
				.email(response.getEmail())
				.firstName(response.getFirstName())
				.lastName(response.getLastName())
				.contactNo(response.getContactNo())
				.userRole(response.getUserRole())
				.build();
	}
	
	@Override
	public ResponseEntity<ResponseStructure<UsersResponse>> saveUser(UsersRequest request) 
	{
		if(request.getUserRole()==UserRole.ADMIN) {
		if(admin==false)
		{
			admin=true;
					try {
						Users user = userRepo.save(mapToUsers(request));
						structure.setStatus(HttpStatus.CREATED.value());
						structure.setMsg("User Saved Successful");
						structure.setData(mapToUserResponse(user));
					}
			catch (Exception e) 
			{
				throw new DuplicateEntryException("Change userName and Email and try again");
			}
		}
		else {
			throw new InvalidUserException("Admin can be only one");
		}
		}
		else {
			try {
				Users user = userRepo.save(mapToUsers(request));
				structure.setStatus(HttpStatus.CREATED.value());
				structure.setMsg("User saved Successful!!!!");
				structure.setData(mapToUserResponse(user));
				
			} catch (Exception e) 
			{
				throw new DuplicateEntryException("Change userName and Email and try again");
			}	
		}
		
		return new ResponseEntity<ResponseStructure<UsersResponse>>(structure,HttpStatus.CREATED);
				
	}

	@Override
	public ResponseEntity<ResponseStructure<UsersResponse>> getUserById(int userId) {
		Users users = userRepo.findById(userId).orElseThrow(()-> new UserNotFoundException("Invalid User ID"));
		structure.setStatus(HttpStatus.FOUND.value());
		structure.setMsg("User Found Successful");
		structure.setData(mapToUserResponse(users));
		return new ResponseEntity<ResponseStructure<UsersResponse>>(structure,HttpStatus.FOUND);
	}

	@Override
	public ResponseEntity<ResponseStructure<UsersResponse>> deleteUserById(int userId) 
	{
		Users user = userRepo.findById(userId).orElseThrow(()-> new UserNotFoundException("Invalid User Id"));
		if(user.isDeleteUser()!=true)
		{
			user.setDeleteUser(true);
			structure.setStatus(HttpStatus.OK.value());
			structure.setMsg("User Delete Successful!!");
			structure.setData(mapToUserResponse(user));
		}
		else {
			structure.setStatus(HttpStatus.OK.value());
			structure.setMsg("User Already Deleted");
			structure.setData(mapToUserResponse(user));
		}
		return new ResponseEntity<ResponseStructure<UsersResponse>>(structure,HttpStatus.OK);
	}

	
}

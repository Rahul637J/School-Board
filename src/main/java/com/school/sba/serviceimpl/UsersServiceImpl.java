package com.school.sba.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Users;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.DuplicateEntryException;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.SchoolNotFound;
import com.school.sba.exception.SubjectNotFoundException;
import com.school.sba.exception.UserIsNotAnAdminException;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.repository.SubjectRepo;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.UsersRequest;
import com.school.sba.responsedto.UsersResponse;
import com.school.sba.service.UserService;
import com.school.sba.util.ResponseStructure;

@Service
public class UsersServiceImpl implements UserService {
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private SubjectRepo subjectRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private ResponseStructure<UsersResponse> structure;

	static boolean admin = false;

	private Users mapToUsers(UsersRequest request) {
		return Users.builder()
				.userName(request.getUserName())
				.email(request.getUserEmail())
				.firstName(request
				.getUserFirstName())
				.lastName(request.getUserLastName())
				.password(passwordEncoder.encode(request.getUserPassword()))
				.contactNo(request.getUserContactNo())
				.userRole(request.getUserRole()).build();
	}

	private UsersResponse mapToUserResponse(Users response) {
		return UsersResponse.builder()
				.userId(response.getUserId())
				.userName(response.getUserName())
				.email(response.getEmail())
				.firstName(response.getFirstName())
				.lastName(response.getLastName())
				.contactNo(response.getContactNo())
				.userRole(response.getUserRole()).build();
	}

	@Override
	public ResponseEntity<ResponseStructure<UsersResponse>> saveAdmin(UsersRequest request) {
		if (request.getUserRole() == UserRole.ADMIN) {

			if (userRepo.existsByUserRole(request.getUserRole()) == false) {
				Users user = userRepo.save(mapToUsers(request));
				structure.setStatus(HttpStatus.CREATED.value());
				structure.setMsg("Admin Created");
				structure.setData(mapToUserResponse(user));
				return new ResponseEntity<ResponseStructure<UsersResponse>>(structure, HttpStatus.CREATED);
			} else {
				throw new DuplicateEntryException("Admin Already Exist");
			}
		} else {
			throw new InvalidUserException("Invalid User!!");
		}
	}

	@Override
	public ResponseEntity<ResponseStructure<UsersResponse>> saveUser(UsersRequest request) 
	{
		String authenticatedName = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepo.findByUserName(authenticatedName).map(user -> {
			
				if (request.getUserRole()!=UserRole.ADMIN) {
					Users user1 = mapToUsers(request);
					user1.setSchool(user.getSchool());
					structure.setStatus(HttpStatus.CREATED.value());
					structure.setMsg(authenticatedName);
					structure.setData(mapToUserResponse(userRepo.save(user1)));
				}
				else
					throw new DuplicateEntryException("Admin Already Existed");
			return new ResponseEntity<ResponseStructure<UsersResponse>> (structure, HttpStatus.CREATED);
		}).orElseThrow(() -> new UserIsNotAnAdminException("admin required!!!"));
	}

	@Override
	public ResponseEntity<ResponseStructure<UsersResponse>> getUserById(int userId) {
		Users users = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("Invalid User ID"));
		structure.setStatus(HttpStatus.FOUND.value());
		structure.setMsg("User Found Successful");
		structure.setData(mapToUserResponse(users));
		return new ResponseEntity<ResponseStructure<UsersResponse>>(structure, HttpStatus.FOUND);
	}

	@Override
	public ResponseEntity<ResponseStructure<UsersResponse>> deleteUserById(int userId) {
		Users user = userRepo.findById(userId).orElseThrow(() -> new UserNotFoundException("Invalid User Id"));
		if (user.isDeleteUser() != true) {
			user.setDeleteUser(true);
			userRepo.save(user);
			structure.setStatus(HttpStatus.OK.value());
			structure.setMsg("User Delete Successful!!");
			structure.setData(mapToUserResponse(user));
		} else if (user.isDeleteUser() == true) {
//			user.setDeleteUser(false);
			structure.setStatus(HttpStatus.OK.value());
			structure.setMsg("User Already Deleted");
			structure.setData(mapToUserResponse(user));
		}
		return new ResponseEntity<ResponseStructure<UsersResponse>>(structure, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ResponseStructure<UsersResponse>> addSubjectToTeacher(int subjectId, int userId) {
		return userRepo.findById(userId).map(user->{
			subjectRepo.findById(subjectId).map(subject->{
				if(user.getUserRole()==UserRole.TEACHER)
				{	
					user.setSubject(subject);
					userRepo.save(user);
					return user;
				}
				else
					throw new IllegalRequestException("Only Teacher can have subject");
			}).orElseThrow(()->new SubjectNotFoundException("Subject is not found"));
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMsg("Subject Added to Teacher");
			structure.setData(mapToUserResponse(user));
			return new ResponseEntity<ResponseStructure<UsersResponse>>(structure,HttpStatus.CREATED);
		}).orElseThrow(()->new UserNotFoundException("User is Not found"));
	}
}

package com.school.sba.serviceimpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.School;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.IllegalRequestException;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.SchoolRequest;
import com.school.sba.responsedto.SchoolResponse;
import com.school.sba.service.SchoolService;
import com.school.sba.util.ResponseStructure;

@Service
public class SchoolServiceImpl implements SchoolService
{
	@Autowired
	private SchoolRepository schoolRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ResponseStructure<SchoolResponse> structure;
	
	private School mapToUsers(SchoolRequest request) {
		return School.builder()
				.schoolName(request.getSchoolName())
				.address(request.getAddress())
				.contactNo(request.getContactNo())
				.emailId(request.getEmailId())
				.build();
	}
	
	private SchoolResponse mapToResponse(School school) {
		return SchoolResponse.builder()
				.schoolId(school.getSchoolId())
				.schoolName(school.getSchoolName())
				.address(school.getAddress())
				.emailId(school.getEmailId())
				.contactNo(school.getContactNo())
				.build();
	}
	
	@Override
	public ResponseEntity<ResponseStructure<SchoolResponse>> saveSchool(int userId, SchoolRequest request) {
	  return userRepo.findById(userId).map(u->{
			if(u.getUserRole()==UserRole.ADMIN)
			{	
				if(u.getSchool()==null) {
					School school = mapToUsers(request);//Saved the new School
					school=schoolRepo.save(school);
				u.setSchool(school);
				userRepo.save(u);//I have updated the User(ADMIN) with the school 
				structure.setStatus(HttpStatus.CREATED.value());
				structure.setMsg("School Saved Successfull!!!");
				structure.setData(mapToResponse(school));
				return new ResponseEntity<ResponseStructure<SchoolResponse>>(structure, HttpStatus.CREATED);
				
			}
				else {
					throw new IllegalRequestException("School Is Already Exist");
				}
		 }
			else {
				throw new IllegalRequestException("Only Admin Can login");
			}
		}
	).orElseThrow(()-> new InvalidUserException("User is not present"));
  }
		
	
	
	
	
	

	
}

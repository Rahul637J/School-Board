package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.AcademicProgram;
import com.school.sba.entity.School;
import com.school.sba.entity.Users;
import com.school.sba.enums.UserRole;
import com.school.sba.exception.AcademicProgramNotFoundById;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.exception.AdminCannotBeAssignedToAcademicException;
import com.school.sba.exception.InvalidUserException;
import com.school.sba.exception.IrreleventTeacherException;
import com.school.sba.exception.SchoolNotFound;
import com.school.sba.exception.UserNotFoundException;
import com.school.sba.exception.UsersNotAssociatedWithAcademicProgram;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.SchoolRepository;
import com.school.sba.repository.SubjectRepo;
import com.school.sba.repository.UserRepo;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.UsersResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@Service
public class AcademicProgramServiceImpl implements AcademicProgramService
{
	@Autowired
	private AcademicProgramRepo academicProgramRepo;
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private ResponseStructure<AcademicProgramResponse> responseStructure;
		
	@Autowired
	private UserRepo userRepo;
		
	AcademicProgramResponse mapToResponse(AcademicProgram academicProgram) {
		return AcademicProgramResponse.builder()
				.programId(academicProgram.getProgramId())
				.programType(academicProgram.getProgramType())
				.programName(academicProgram.getProgramName())
				.beginsAt(academicProgram.getBeginsAt())
				.endsAt(academicProgram.getEndsAt())
				.schoolId(academicProgram.getSchool().getSchoolId())
				.build();
	}
	
	public AcademicProgram mapToAcademicProgram(AcademicProgramRequest request) {
		return AcademicProgram.builder()
				.programType(request.getProgramType())
				.programName(request.getProgramName())
				.beginsAt(request.getBeginsAt())
				.endsAt(request.getEndsAt())
				.build();
	}
	
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicProgram(int schoolId,
			AcademicProgramRequest request) {
		School school = schoolRepository.findById(schoolId).orElseThrow(()-> new SchoolNotFound("Invalid School Id"));
		
		AcademicProgram academicProgram = academicProgramRepo.save(mapToAcademicProgram(request));		
		school.getAcademicProgramsList().add(academicProgram);
		school=schoolRepository.save(school);
		academicProgram.setSchool(school);
		academicProgram=academicProgramRepo.save(academicProgram);
		responseStructure.setStatus(HttpStatus.CREATED.value());
		responseStructure.setMsg("AcademicProgram Saved Successfull!!");
		responseStructure.setData(mapToResponse(academicProgram));
		return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,HttpStatus.CREATED);
	}
	
	@Override
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> findAllAcademicProgram(int schoolId) {
		return schoolRepository.findById(schoolId).map(school->{
			List<AcademicProgram> academicProgramsList = school.getAcademicProgramsList();
			ResponseStructure<List<AcademicProgramResponse>> rs=new ResponseStructure<>();
			List<AcademicProgramResponse> list=new ArrayList<>();
			for(AcademicProgram ac:academicProgramsList)
			{
				list.add(mapToResponse(ac));
			}
			rs.setStatus(HttpStatus.FOUND.value());
			rs.setMsg("Academic Program Found!!!!");
			rs.setData(list);
			return new ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>>(rs,HttpStatus.FOUND);
		}).orElseThrow(()-> new SchoolNotFound("Invalid School Id"));
		}	
	
	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> assignTeachersAndStudent(int programId,int userId) {
		return userRepo.findById(userId).map(user->{
			return academicProgramRepo.findById(programId).map(program->{
				if(user.getUserRole()!=UserRole.ADMIN)
				{
					if(user.getUserRole()==UserRole.STUDENT)
					{
						System.out.println("From Student"+user.getUserRole());
						program.getUsersList().add(user);
						academicProgramRepo.save(program);
					}
					
					if(user.getUserRole()==UserRole.TEACHER)
					 {
						System.out.println("From Teacher");
							 if(program.getSubject().contains(user.getSubject())){
									program.getUsersList().add(user);
									academicProgramRepo.save(program);
									}
									else
										throw new IrreleventTeacherException("The Subject of the Teacher is irreveleant to academic program subject ");
					 }
					responseStructure.setStatus(HttpStatus.CREATED.value());
					responseStructure.setMsg(user.getUserRole()+" added is Added to academic Program");
					responseStructure.setData(mapToResponse(program));
					return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,HttpStatus.CREATED);
				}
				else
					throw new AdminCannotBeAssignedToAcademicException("Admin cannot assigned to the Academic program");
			}).orElseThrow(()-> new AcademicProgramNotFoundById("Academic Program is not found in given ID"));
		}).orElseThrow(()->new UserNotFoundException("User is not found in the given ID"));
         }

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> deleteAcademicProgram(int academicProgramId) 
	{
		return academicProgramRepo.findById(academicProgramId).map(program->{
			if(program.isDelete()==false)
			{
				program.setDelete(true);
				academicProgramRepo.save(program);
				responseStructure.setStatus(HttpStatus.OK.value());
				responseStructure.setMsg("AcademicProgram Deleted Successful!!!");
				responseStructure.setData(mapToResponse(program));
			}
			else {
				responseStructure.setStatus(HttpStatus.OK.value());
				responseStructure.setMsg("AcademicProgram Already Deleted!!!");
				responseStructure.setData(mapToResponse(program));
			}
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(responseStructure,HttpStatus.OK);
		}).orElseThrow(()-> new AcademicProgramNotFoundById("Invalid Academic Program ID"));
		
	}			
}
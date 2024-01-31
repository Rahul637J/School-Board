package com.school.sba.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.school.sba.entity.Users;
import com.school.sba.enums.UserRole;
import com.school.sba.requestdto.AcademicProgramRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.UsersResponse;
import com.school.sba.service.AcademicProgramService;
import com.school.sba.util.ResponseStructure;

@RestController
public class AcademicProgramController 
{
	@Autowired
	private AcademicProgramService academicservice; 
	
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addAcademicProgram(@PathVariable int schoolId,@RequestBody AcademicProgramRequest request )
	{
		return academicservice.addAcademicProgram(schoolId,request);
	}
	
	@GetMapping("/schools/{schoolId}/academic-programs")
	public ResponseEntity<ResponseStructure<List<AcademicProgramResponse>>> getAllAcademicProgram(@PathVariable int schoolId)
	{
		return academicservice.findAllAcademicProgram(schoolId);
	}
	
	@PutMapping("/academic-programs/{programId}/users/{userId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> assignTeachersAndStudent(@PathVariable int programId,@PathVariable int userId)
	{
		return academicservice.assignTeachersAndStudent(programId,userId);
	}
	
	@DeleteMapping("/delete/{academicProgramId}")
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> deleteAcademicProgram(@PathVariable int academicProgramId)
	{
		return academicservice.deleteAcademicProgram(academicProgramId);
	}
}

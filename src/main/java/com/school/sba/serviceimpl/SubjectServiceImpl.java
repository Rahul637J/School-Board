package com.school.sba.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.school.sba.entity.Subject;
import com.school.sba.exception.AcademicProgramNotFoundException;
import com.school.sba.exception.SubjectNotAddedException;
import com.school.sba.repository.AcademicProgramRepo;
import com.school.sba.repository.SubjectRepo;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.service.SubjectService;
import com.school.sba.util.ResponseStructure;

@Service
public class SubjectServiceImpl implements SubjectService {

	@Autowired
	private SubjectRepo subjectRepo;

	@Autowired
	private AcademicProgramRepo academicProgramRepo;

	@Autowired
	private ResponseStructure<AcademicProgramResponse> structure;

	@Autowired
	private AcademicProgramServiceImpl academicProgramServiceImpl;
	
	private SubjectResponse mapToResponse(Subject subject) {
		return SubjectResponse.builder()
				.subjectId(subject.getSubjectId())
				.subjectName(subject.getSubjectName())
				.build();
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubject(SubjectRequest subjectRequest,
			int programId) {
		return academicProgramRepo.findById(programId).map(program -> {
			List<Subject> subjects = new ArrayList<Subject>();
			subjectRequest.getSubjectName().forEach(name -> {
				Subject subject = subjectRepo.findBySubjectName(name).map(s -> s).orElseGet(() -> {
					Subject subject2 = new Subject();
					subject2.setSubjectName(name);
					subjectRepo.save(subject2);
					return subject2;
				});
				subjects.add(subject);
			});
			program.setSubject(subjects);
			academicProgramRepo.save(program);
			structure.setStatus(HttpStatus.CREATED.value());
			structure.setMsg("Updated the Subject list to Academic Program");
			structure.setData(academicProgramServiceImpl.mapToResponse(program));
			return new ResponseEntity<ResponseStructure<AcademicProgramResponse>>(structure, HttpStatus.CREATED);
		}).orElseThrow(() -> new AcademicProgramNotFoundException("Academic program Not found for given id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjects(int programId,
			SubjectRequest subjectRequest) {

		return academicProgramRepo.findById(programId).map(program -> {
			// Remove the existing subjects for the program
			program.getSubject().clear();
			academicProgramRepo.save(program);

			return addSubject(subjectRequest, programId);

		}).orElseThrow(() -> new AcademicProgramNotFoundException("Academic program Not found for given id"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<SubjectResponse>>> getAllSubject() 
	{
		 List<Subject> subjectList = subjectRepo.findAll();
		 if(subjectList.size()!=0)
		 {
			 ArrayList<SubjectResponse>a=new ArrayList<>();
			 subjectList.forEach(subject->{
				 a.add(mapToResponse(subject));
			 });
			 ResponseStructure<List<SubjectResponse>> responseStructure=new ResponseStructure<>();
			 responseStructure.setStatus(HttpStatus.FOUND.value());
			 responseStructure.setMsg("List of Subjects Found Successfully!!!");
			 responseStructure.setData(a);
			 return new ResponseEntity<ResponseStructure<List<SubjectResponse>>>(responseStructure,HttpStatus.FOUND);
		 }
		 else
			 throw new SubjectNotAddedException("Not Found");
	}
}

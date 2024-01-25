package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.entity.Subject;
import com.school.sba.requestdto.SubjectRequest;
import com.school.sba.responsedto.AcademicProgramResponse;
import com.school.sba.responsedto.SubjectResponse;
import com.school.sba.util.ResponseStructure;

public interface SubjectService {

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> addSubject(SubjectRequest subjectRequest, int programId);

	ResponseEntity<ResponseStructure<AcademicProgramResponse>> updateSubjects(int programId,
			SubjectRequest subjectRequest);

	ResponseEntity<ResponseStructure<List<SubjectResponse>>>

			getAllSubject();

}

package com.school.sba.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.school.sba.requestdto.ClassHourRequest;
import com.school.sba.requestdto.ClassHourRequestUpdate;
import com.school.sba.responsedto.ClassHourResponse;
import com.school.sba.util.ResponseStructure;

public interface ClassHourService
{

	ResponseEntity<ResponseStructure<String>> generateClassHourForAcademicProgram(int programId);

	ResponseEntity<ResponseStructure<List<ClassHourResponse>>> updateClassHour(List<ClassHourRequestUpdate> classHourRequest);
	

}
